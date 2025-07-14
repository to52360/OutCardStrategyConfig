package lin.weightHandler

import lin.weightHandler.condition.bean.CardWeightInfo

interface CardWeightInfoProvide {
    fun getInfos():Map<String, CardWeightInfo>
}