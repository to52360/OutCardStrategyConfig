package lin.weightHandler.condition

import club.xiaojiawei.config.log
import lin.bean.*
import lin.dao.ComboCard
import lin.dao.WarInfo
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.context.ConditionException
import java.util.*
import kotlin.collections.HashMap

class ConditionWeightHandler:WeightHandler,InitHandler {

    private val metadataKey = MetadataKey<OutCardCondition>("ConditionWeightHandler")
    private val needManageCondition = LinkedList<OutCardCondition>()


    private fun loadConfig():List<ConditionGroup>{
        TODO()
    }
    override   fun priority()=5
    override fun cardWeightProcess(callCard: ComboCard, warInfo: WarInfo) {
        callCard.getCondition()?.canUse(callCard,warInfo)
    }
    //todo  自定义配置
    override fun init(infos: List<ComboWeightInfo>) {
         val groupCondition : HashMap<Int,OutCardCondition> = hashMapOf()
         val conditionConfigs : List<ConditionGroup> = loadConfig()
         ServiceLoader.load(OutCardCondition::class.java).forEach {
            groupCondition[it.id()] = it
         }
        val weightGroupInfos =  infos.groupBy { it.groupId }
        conditionConfigs.forEach{
            conditionGroup->
            val outCardCondition = groupCondition[conditionGroup.outCardConditionId]
            outCardCondition?.let{
                //todo 这里只处理依赖权重表的情况
                //这里采用反射复制,没有采用工厂模式
                val copyCondition = outCardCondition.copy()
/*                if(copyCondition is DepByWeightInfo){
                    if(conditionGroup is ConditionByWeightId){//todo 感觉有问题这么多if,逻辑也乱的
                        val binWeightInfos = weightGroupInfos[conditionGroup.bindId]
                        if(binWeightInfos==null) {
                            errorProcess("没有在权重表找到对应组数据,id为${conditionGroup.depByWeightId}")
                            return
                        }
                        val depWeightInfos = weightGroupInfos[conditionGroup.depByWeightId]
                        if(depWeightInfos == null ){
                            val msg = "没有在权重表找到对应组数据,id为${conditionGroup.depByWeightId}"
                            errorProcess(msg)
                            return
                        }
                        copyCondition.initByWeightInfo(depWeightInfos)
                        needManageCondition.add(copyCondition)
                        binWeightInfos.forEach{
                            //在卡牌数据冗余打出条件
                            it.metadata.put(metadataKey,copyCondition)
                        }

                    }

                }*/
            }?:run{//没有对应条件id实现
                val msg = "${conditionGroup.groupId}没有匹配到conditionId:${conditionGroup.outCardConditionId}的信息"
               errorProcess(msg)

            }
        }


    }
    @Throws(ConditionException::class)
    private fun  errorProcess(msg:String) {
        log.error {
            msg
        }
        throw ConditionException(msg)
    }
    private fun OutCardCondition.copy(): OutCardCondition {
        val clazz = this::class.java
        try {
            val primaryConstructor = clazz.getConstructor()
            return  primaryConstructor.newInstance()
        }catch (e:NoSuchMethodException){
            log.error{
                "${clazz.simpleName}没有无参构建函数"
            }
            throw e
        }


    }
}