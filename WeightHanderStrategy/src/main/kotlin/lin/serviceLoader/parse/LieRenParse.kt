package lin.serviceLoader.parse

import lin.bean.CardWeightInfo

class LieRenParse : ParseCardWeightInfo {
    override fun parse(infoMap: Map<String, CardWeightInfo>) {
        infoMap["WW_807"]?.toDie = true
    }
}