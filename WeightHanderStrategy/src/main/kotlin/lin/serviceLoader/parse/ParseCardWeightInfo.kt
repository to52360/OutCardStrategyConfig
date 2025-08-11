package lin.serviceLoader.parse

import lin.bean.CardWeightInfo

interface ParseCardWeightInfo {
    fun parse(infoMap: Map<String, CardWeightInfo>)
}