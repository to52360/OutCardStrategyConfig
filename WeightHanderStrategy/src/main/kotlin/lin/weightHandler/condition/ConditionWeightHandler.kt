package lin.weightHandler.condition


import lin.bean.*
import lin.domain.MyWarManage
import lin.myLog
import lin.serviceLoader.weightRule.DepByWeightGroupId
import lin.serviceLoader.weightRule.DepByWeightInfos
import lin.serviceLoader.weightRule.WeightCondition
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.config.GroupStrategyDao
import lin.weightHandler.condition.context.ConditionException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


import kotlin.collections.HashMap

/**
 * 条件权重处理器
 */
class ConditionWeightHandler : WeightHandler, InitHandler, KoinComponent {


    /**
     * todo 没有配置信息ui 暂时硬编码 获取配置
     */
    private fun loadConfig(): List<ConditionGroup>? {
        val groupStrategyDao: GroupStrategyDao by inject()
        return groupStrategyDao.getAll()
    }

    //todo-future 存在魔数
    override fun priority() = 5
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        val weightCalculate = callCard.weightRules
        weightCalculate.forEach {
            myLog.info { "条件处理权重前的权重值:${callCard.powerWeight}" }
            it.calculateSetWeight(callCard, warManage)
            myLog.info { "条件处理权重后的权重值:${callCard.powerWeight}" }
        }
    }

    /**
     * 初始化,为卡牌冗余条件计算
     */
    override fun init(infos: List<CardWeightInfo>) {
        //条件组信息
        val weightGroupInfos: List<ConditionGroup> = loadConfig() ?: return


        //条件实现数据
        val groupCondition: HashMap<Int, WeightCondition> = hashMapOf()
        ServiceLoaderUtils.loadServices(WeightCondition::class.java).forEach {
            groupCondition[it.id()] = it
        }
        myLog.info {
            "加载到的条件组id:${groupCondition.keys}"
        }


        //权重分组信息
        val weightInfoGroups = infos.groupBy { it.groupId }
        //遍历解析组信息
        weightGroupInfos.forEach { conditionGroup ->
            val weightConditionId = conditionGroup.weightConditionId
            val weightCondition = groupCondition[weightConditionId]
            //获取到对应id条件实现
            weightCondition?.let {

                //从权重表获取绑定数据数据
                val binWeightInfos = weightInfoGroups[conditionGroup.bindId]
                if (binWeightInfos == null) {
                    val msg = "条件组需要绑定的数据没有在权重表找到,weight(bindId)为${conditionGroup.bindId}"
                    throw ConditionException(msg)
                }
                //这里采用反射复制,为了简洁和快速实现 没有采用工厂模式
                val copyCondition = it.copy()


                //todo 逻辑存在问题

                //依赖数据处理
                if (copyCondition is DepByWeightInfos) {
                    //todo-future 万一以类型绑定卡牌,那打出条件如何冗余在卡牌信息里
                    //绑定对象,
                    val depWeightInfos = mutableListOf<List<CardWeightInfo>>()
                    conditionGroup.depByWeightIds.forEach { depId ->
                        weightInfoGroups[depId]?.run {
                            depWeightInfos.add(this)
                        }
                    }


                    if (depWeightInfos.isEmpty()) {
                        val msg =
                            "条件组需要绑定的数据没有在权重表找到,weight(depByWeightId)为${conditionGroup.depByWeightIds}"
                        throw ConditionException(msg)

                    }
                    copyCondition.initByWeightInfos(depWeightInfos)
                } else if (copyCondition is DepByWeightGroupId) {
                    if (conditionGroup.depByWeightIds.isEmpty()) {
                        myLog.warn { "找不到对应分组信息${conditionGroup.depByWeightIds}" }
                    }
                    copyCondition.initByGroupIds(conditionGroup.depByWeightIds)

                }

                //完善条件信息
                copyCondition.groupWeight = conditionGroup.basePriority


                //在卡牌数据冗余打出条件
                binWeightInfos.forEach { info ->
                    info.addWeightRule(copyCondition)
                }


            } ?: run {//没有对应条件id实现
                val msg =
                    "groupId=${conditionGroup.groupId},没有匹配到conditionId:${conditionGroup.weightConditionId}的条件信息"
                throw ConditionException(msg)

            }
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