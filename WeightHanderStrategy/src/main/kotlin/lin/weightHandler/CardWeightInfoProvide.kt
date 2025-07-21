package lin.weightHandler

import lin.bean.CardWeightInfo

interface CardWeightInfoProvide {
    fun getInfos():Map<String, CardWeightInfo>
}