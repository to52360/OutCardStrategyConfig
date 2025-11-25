package lin.weightHandler.condition

import lin.bean.CardWeightInfo
import lin.config.ConfigDispatcher
import lin.lifecycle.LifecycleRegister
import lin.myLog
import lin.serviceLoader.weightRule.*
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.condition.bean.ConditionGroup
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 目的 降低ConditionWeightHandler的复杂
 * [ConditionWeightHandler]
 */
class ConditionHandlerInit(infos: List<CardWeightInfo>, val configDispatcher: ConfigDispatcher) : KoinComponent {
    val groupCondition: HashMap<String, RuleInfo> = hashMapOf()
    val weightGroupInfos = infos.groupBy { it.groupId }
    val lifecycleRegister = get<LifecycleRegister>()
    init {
        ServiceLoaderUtils.loadServices(RuleInfo::class.java).forEach {
            groupCondition[it.ruleId()] = it
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
                    myLog.warn { "条件组需要绑定的数据没有在权重表找到,weight(bindId)为${conditionGroup.bindId[0]}" }
                    return
                }
                bindWeightInfos.addAll(weightGroupInfo)
            }

            //这里采用反射复制,为了简洁和快速实现 没有采用工厂模式
            val copyCondition = it.copy()
            //处理依赖
            if (processDep(copyCondition, conditionGroup)) {
                //冗余信息
                bind(copyCondition, bindWeightInfos)
            }


        } ?: run {//没有对应条件id实现
            val msg =
                "groupId=${conditionGroup.groupId},没有匹配到conditionId:${conditionGroup.weightConditionId}的条件信息"
            myLog.warn { msg }
            return

        }
    }

    private fun bind(ruleInfo: RuleInfo, bindWeightInfos: List<CardWeightInfo>) {
        //在卡牌数据冗余打出条件
        if (ruleInfo is WeightRule) {
            bindWeightInfos.forEach { info ->
                info.setWeightRule(ruleInfo)
            }
        }
        if (ruleInfo is IntentRule) {
            bindWeightInfos.forEach { info ->
                info.setIntentRule(ruleInfo)
            }
        }

        //绑定配置信息
        if (ruleInfo is ExtConfig) {
            val cardConfigs = ruleInfo.cardConfigs()
            configDispatcher.dispatch(cardConfigs, bindWeightInfos)
        }

        lifecycleRegister.register(ruleInfo)


    }

    private fun processDep(ruleInfo: RuleInfo, conditionGroup: ConditionGroup): Boolean {
        //组权重处理
        ruleInfo.groupWeight = conditionGroup.groupWeight
        ruleInfo.setUnCondWeight(conditionGroup.unConditionWeight)
        ruleInfo.setNum(conditionGroup.num ?: 0)

        if (ruleInfo is DepProcessor) {
            return ruleInfo.processAndVerify(conditionGroup, weightGroupInfos)
        }

        return true
    }

    //都是通过ServerLoader加载没有可能获取不到
    private fun RuleInfo.copy(): RuleInfo {
        val clazz = this::class.java
        //都是通过ServerLoader加载没有可能获取不到
        val primaryConstructor = clazz.getConstructor()
        return primaryConstructor.newInstance()
    }


}