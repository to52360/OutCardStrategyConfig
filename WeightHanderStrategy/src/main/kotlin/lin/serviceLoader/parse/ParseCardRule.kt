package lin.serviceLoader.parse

import lin.bean.CardWeightInfo
import lin.myLog
import lin.serviceLoader.weightRule.CardRule
import lin.utils.serviceLoader.ServiceLoaderUtils

/**
 * 单卡条件绑定并绑定
 */
class ParseCardRule : ParseCardWeightInfo {
    override fun parse(infoMap: Map<String, CardWeightInfo>) {
        ServiceLoaderUtils.loadServices(CardRule::class.java).forEach {
            val card = infoMap[it.cardId()]
            card?.run {
                myLog.info { "单卡规则加载到:${it.cardId()}" }
                addWeightRule(it)
            }
        }
    }
}