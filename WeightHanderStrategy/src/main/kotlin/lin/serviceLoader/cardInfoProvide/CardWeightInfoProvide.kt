package lin.serviceLoader.cardInfoProvide

import lin.bean.CardWeightInfo

interface CardWeightInfoProvide {
    fun getInfos():Map<String, CardWeightInfo>
}