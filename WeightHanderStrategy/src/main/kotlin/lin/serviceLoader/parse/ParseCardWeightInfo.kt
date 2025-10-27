package lin.serviceLoader.parse

import lin.bean.CardWeightInfo

/**
 * 用于绑定数据
 */
interface ParseCardWeightInfo {
    fun parse(infoMap: Map<String, CardWeightInfo>)
}