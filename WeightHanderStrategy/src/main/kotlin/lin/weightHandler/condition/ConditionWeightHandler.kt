package lin.weightHandler.condition


import lin.bean.ComboCard
import lin.dao.MyWarManage
import lin.myLog
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.bean.CardWeightInfo
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.bean.MetadataKey
import lin.weightHandler.condition.context.ConditionException
import lin.weightHandler.condition.define.DepByWeightInfo
import lin.weightHandler.condition.define.WeightCondition
import java.util.ServiceLoader

import kotlin.collections.HashMap

/**
 * 条件权重处理器
 */
class ConditionWeightHandler : WeightHandler, InitHandler {

    private val metadataKey = MetadataKey<WeightCondition>("ConditionWeightHandler")
    private val needManageCondition = mutableListOf<WeightCondition>()

    /**
     * todo 没有配置信息ui 暂时硬编码 获取配置
     */
    private fun loadConfig(): List<ConditionGroup> {
        return listOf(ConditionGroup(1, 3.0, 250625013, 2.0))
    }
    //todo-future 存在魔数
    override fun priority() = 5
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        //
        callCard.getMetadata(metadataKey)?.let {
            myLog.info { "条件处理权重前的权重值:${callCard.varPowerWeight}" }
            it.calculateSetWeight(callCard, warManage)
            myLog.info { "条件处理权重后的权重值:${callCard.varPowerWeight}" }
        }
    }

    /**
     * todo-future  自定义配置未实现 [lin.weightHandler.condition.bean.ConditionByCustomize]
     */
    override fun init(infos: List<CardWeightInfo>) {
        //条件组信息
        val conditionGroupInfos: List<ConditionGroup> = loadConfig()


        //条件实现数据
        val groupCondition: HashMap<Int, WeightCondition> = hashMapOf()
        ServiceLoader.load(WeightCondition::class.java).forEach {
            groupCondition[it.id()] = it
        }
        myLog.info {
            "加载到的条件组id:${groupCondition.keys}"
        }


        val weightGroupInfos = infos.groupBy { it.groupId }


        conditionGroupInfos.forEach { conditionGroup ->
            val outCardConditionId = conditionGroup.outCardConditionId
            val outCardCondition = groupCondition[outCardConditionId]
            outCardCondition?.let {

                //主数据处理
                val binWeightInfos = weightGroupInfos[conditionGroup.bindId]
                if (binWeightInfos == null) {
                    val msg = "条件组需要绑定的数据没有在权重表找到,weight(id)为${conditionGroup.bindId}"
                    throw ConditionException(msg)
                }
                //这里采用反射复制,为了简洁和快速实现 没有采用工厂模式
                val copyCondition = it.copy()


                //依赖数据处理
                if (copyCondition is DepByWeightInfo) {
                    //todo-future 万一以类型绑定卡牌,那打出条件如何冗余在卡牌信息里
                    //绑定对象,

                    val depWeightInfos = weightGroupInfos[conditionGroup.depByWeightId]
                    if (depWeightInfos == null) {
                        val msg = "条件组需要绑定的数据没有在权重表找到,weight(id)为${conditionGroup.depByWeightId}"
                        throw ConditionException(msg)

                    }
                    copyCondition.initByWeightInfo(depWeightInfos)
                }


                //在卡牌数据冗余打出条件
                binWeightInfos.forEach { info ->

                    info.putMetadata(metadataKey, copyCondition)
                }


                //todo-future 生命周期,游戏开始,结束调用对应方法,为了条件组有状态
                needManageCondition.add(copyCondition)


            } ?: run {//没有对应条件id实现
                val msg = "groupId=${conditionGroup.groupId},没有匹配到conditionId:${conditionGroup.outCardConditionId}的条件信息"
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