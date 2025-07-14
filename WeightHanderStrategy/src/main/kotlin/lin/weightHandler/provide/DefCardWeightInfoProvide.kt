package lin.weightHandler.provide

import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import lin.weightHandler.CardWeightInfoProvide
import lin.weightHandler.condition.bean.CardWeightInfo

class DefCardWeightInfoProvide : CardWeightInfoProvide {
    override fun getInfos(): Map<String, CardWeightInfo> {
        val weightConfigs = CARD_WEIGHT_TRIE.data()
        return weightConfigs.associateBy(
            keySelector = { it.key }
        ) { weightCard ->
            CardWeightInfo(weightCard.key,  weightCard.value.powerWeight,weightCard.value.weight)
        }
    }
}