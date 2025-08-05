package lin.weightHandler.condition

import lin.bean.CardWeightInfo
import lin.myLog
import lin.serviceLoader.cardRule.WeightCondition
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.config.WeightGroupConfig

/**
 * 目的 降低ConditionWeightHandler的复杂
 * [ConditionWeightHandler]
 */
class ConditionInitDomain(infos: List<CardWeightInfo>) {
    val groupCondition: HashMap<Int, WeightCondition> = hashMapOf()
    val weightGroupInfos = infos.groupBy { it.groupId }
    init {
        ServiceLoaderUtils.loadServices(WeightCondition::class.java).forEach {
            groupCondition[it.id()] = it
        }
        myLog.info {
            "加载到的条件组id:${groupCondition.keys}"
        }

    }

}