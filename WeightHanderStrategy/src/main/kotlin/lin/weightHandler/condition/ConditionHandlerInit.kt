package lin.weightHandler.condition

import lin.bean.CardWeightInfo
import lin.lifecycle.LifecycleRegister
import lin.myLog
import lin.serviceLoader.weightRule.DepByWeightGroupId
import lin.serviceLoader.weightRule.DepByWeightInfos
import lin.serviceLoader.weightRule.WeightCondition
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.context.ConditionException
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 目的 降低ConditionWeightHandler的复杂
 * [ConditionWeightHandler]
 */
class ConditionHandlerInit(infos: List<CardWeightInfo>) : KoinComponent {
    val groupCondition: HashMap<String, WeightCondition> = hashMapOf()
    val weightGroupInfos = infos.groupBy { it.groupId }
    val lifecycleRegister = get<LifecycleRegister>()
    init {
        ServiceLoaderUtils.loadServices(WeightCondition::class.java).forEach {
            groupCondition[it.id()] = it
        }
        myLog.info {
            "加载到的条件组id:${groupCondition.keys}"
        }

    }

    fun parseConditionGroup(conditionGroup: ConditionGroup) {
        val weightConditionId = conditionGroup.weightConditionId
        val weightCondition = groupCondition[weightConditionId]
        //获取到对应id条件实现
        weightCondition?.let {

            //从权重表获取绑定数据数据
            val bindWeightInfos = mutableListOf<CardWeightInfo>()
            for (bindId in conditionGroup.bindId) {
                val weightGroupInfo = weightGroupInfos[bindId]
                if (weightGroupInfo == null) {
                    val msg = "条件组需要绑定的数据没有在权重表找到,weight(bindId)为${conditionGroup.bindId}"
                    myLog.warn { msg }
                    return
                }
                bindWeightInfos.addAll(weightGroupInfo)
            }

            //这里采用反射复制,为了简洁和快速实现 没有采用工厂模式
            val copyCondition = it.copy()
            //处理依赖
            processDep(copyCondition, conditionGroup)
            //完善条件信息
            copyCondition.groupWeight = conditionGroup.basePriority
            //冗余信息
            bind(copyCondition, bindWeightInfos)


        } ?: run {//没有对应条件id实现
            val msg =
                "groupId=${conditionGroup.groupId},没有匹配到conditionId:${conditionGroup.weightConditionId}的条件信息"
            myLog.warn { msg }
            return

        }
    }

    private fun bind(weightCondition: WeightCondition, bindWeightInfos: List<CardWeightInfo>) {
        //在卡牌数据冗余打出条件
        bindWeightInfos.forEach { info ->
            info.setWeightRule(weightCondition)
        }
        lifecycleRegister.register(weightCondition)
    }

    private fun processDep(weightCondition: WeightCondition, conditionGroup: ConditionGroup) {
        //依赖数据处理
        if (weightCondition is DepByWeightInfos) {
            //todo-future 万一以类型绑定卡牌,那打出条件如何冗余在卡牌信息里
            //绑定对象,
            val depWeightInfos = mutableListOf<List<CardWeightInfo>>()
            conditionGroup.depByWeightIds.forEach { depId ->
                weightGroupInfos[depId]?.run {
                    depWeightInfos.add(this)
                }
            }


            if (depWeightInfos.isEmpty()) {
                val msg =
                    "条件组需要绑定的数据没有在权重表找到,weight(depByWeightId)为${conditionGroup.depByWeightIds}"
                throw ConditionException(msg)

            }
            weightCondition.initByWeightInfos(depWeightInfos)
        } else if (weightCondition is DepByWeightGroupId) {
            if (conditionGroup.depByWeightIds.isEmpty()) {
                myLog.warn { "找不到对应分组信息${conditionGroup.depByWeightIds}" }
            }
            weightCondition.initByGroupIds(conditionGroup.depByWeightIds)

        }
    }

    //都是通过ServerLoader加载没有可能获取不到
    private fun WeightCondition.copy(): WeightCondition {
        val clazz = this::class.java
        //都是通过ServerLoader加载没有可能获取不到
        val primaryConstructor = clazz.getConstructor()
        return primaryConstructor.newInstance()
    }


}