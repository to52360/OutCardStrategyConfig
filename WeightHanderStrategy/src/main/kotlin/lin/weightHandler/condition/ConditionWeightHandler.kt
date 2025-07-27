package lin.weightHandler.condition


import lin.bean.*
import lin.domain.MyWarManage
import lin.myLog
import lin.serviceLoader.cardRule.DepByWeightInfos
import lin.serviceLoader.cardRule.WeightCondition
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.config.WeightGroupConfig
import lin.weightHandler.condition.context.ConditionException


import kotlin.collections.HashMap
/**
 * 条件权重处理器
 */
class ConditionWeightHandler : WeightHandler, InitHandler {


    private val needManageCondition = mutableListOf<WeightCondition>()

    /**
     * todo 没有配置信息ui 暂时硬编码 获取配置
     */
    private fun loadConfig(): List<ConditionGroup>? {

        return WeightGroupConfig().configs()
    }
    //todo-future 存在魔数
    override fun priority() = 5
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        val weightCalculate = callCard.weightCalculate
        if(weightCalculate is OutCondition){
            myLog.info { "条件处理权重前的权重值:${callCard.varPowerWeight}" }
            weightCalculate.calculateSetWeight(callCard, warManage)
            myLog.info { "条件处理权重后的权重值:${callCard.varPowerWeight}" }
        }else if(weightCalculate is OutConditions){//多条件处理
            myLog.info { "条件处理权重前的权重值:${callCard.varPowerWeight}" }
            weightCalculate.calculateSetWeight(callCard, warManage)
            myLog.info { "条件处理权重后的权重值:${callCard.varPowerWeight}" }
        }

    }

    /**
     * 初始化,为卡牌冗余条件计算
     */
    override fun init(infos: List<CardWeightInfo>) {
        //条件组信息
        val conditionGroupInfos: List<ConditionGroup> = loadConfig()?:return


        //条件实现数据
        val groupCondition: HashMap<Int, WeightCondition> = hashMapOf()
        ServiceLoaderUtils.loadServices(WeightCondition::class.java).forEach {
            groupCondition[it.id()] = it
        }
        myLog.info {
            "加载到的条件组id:${groupCondition.keys}"
        }


        val weightGroupInfos = infos.groupBy { it.groupId }

        //遍历解析组信息
        conditionGroupInfos.forEach { conditionGroup ->
            val outCardConditionId = conditionGroup.weightConditionId
            val outCardCondition = groupCondition[outCardConditionId]
            //获取到对应id条件实现
            outCardCondition?.let {

                //从权重表获取依赖数据
                val binWeightInfos = weightGroupInfos[conditionGroup.bindId]
                if (binWeightInfos == null) {
                    val msg = "条件组需要绑定的数据没有在权重表找到,weight(bindId)为${conditionGroup.bindId}"
                    throw ConditionException(msg)
                }
                //这里采用反射复制,为了简洁和快速实现 没有采用工厂模式
                val copyCondition = it.copy()


                //依赖数据处理
                if (copyCondition is DepByWeightInfos) {
                    //todo-future 万一以类型绑定卡牌,那打出条件如何冗余在卡牌信息里
                    //绑定对象,
                    val depWeightInfos = mutableListOf<List<CardWeightInfo>>()
                    conditionGroup.depByWeightIds.forEach { depId ->
                        weightGroupInfos[depId]?.run {
                            depWeightInfos.add(this)
                        }
                    }


                    if (depWeightInfos.isEmpty()) {
                        val msg = "条件组需要绑定的数据没有在权重表找到,weight(depByWeightId)为${conditionGroup.depByWeightIds}"
                        throw ConditionException(msg)

                    }
                    copyCondition.initByWeightInfo(depWeightInfos)
                }

                //完善条件信息
                copyCondition.groupWeight = conditionGroup.basePriority
                //这里共用一个weightCalculate
                val weightCalculate = OutCondition(copyCondition)


                //在卡牌数据冗余打出条件
                binWeightInfos.forEach { info ->
                    info.weightCalculate = weightCalculate
                }


                //todo-future 生命周期,游戏开始,结束调用对应方法,为了条件组有状态
                needManageCondition.add(copyCondition)


            } ?: run {//没有对应条件id实现
                val msg = "groupId=${conditionGroup.groupId},没有匹配到conditionId:${conditionGroup.weightConditionId}的条件信息"
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