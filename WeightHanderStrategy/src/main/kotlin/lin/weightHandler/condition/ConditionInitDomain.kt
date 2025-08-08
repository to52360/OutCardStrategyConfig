package lin.weightHandler.condition

import lin.bean.CardWeightInfo
import lin.myLog
import lin.serviceLoader.weightRule.WeightCondition
import lin.utils.serviceLoader.ServiceLoaderUtils

/**
 * 目的 降低ConditionWeightHandler的复杂
 * [ConditionWeightHandler]
 */
class ConditionInitDomain(infos: List<CardWeightInfo>) {
    val groupCondition: HashMap<String, WeightCondition> = hashMapOf()
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