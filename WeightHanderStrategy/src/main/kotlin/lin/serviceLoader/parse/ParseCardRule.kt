package lin.serviceLoader.parse

import lin.bean.CardWeightInfo
import lin.serviceLoader.weightRule.CardRule
import lin.utils.serviceLoader.ServiceLoaderUtils

/**
 * 解析单卡条件并绑定
 */
class ParseCardRule : ParseCardWeightInfo {
    override fun parse(infoMap: Map<String, CardWeightInfo>) {
        ServiceLoaderUtils.loadServices(CardRule::class.java).forEach {
            val card = infoMap[it.cardId()]
            card?.run {
                addWeightRule(it)
            }
        }
    }
}