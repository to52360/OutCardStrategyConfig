package lin.util

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import club.xiaojiawei.bean.SimulateWeightCard
import club.xiaojiawei.config.log
import club.xiaojiawei.data.CARD_WEIGHT_TRIE

import kotlin.collections.ArrayList

object StrategyUtil {
    fun sortCardByPowerWeight(cards: List<SimulateWeightCard>): List<SimulateWeightCard> {
        return cards.sortedByDescending{
            it.powerWeight = CARD_WEIGHT_TRIE[it.card.cardId]?.powerWeight ?: 1.0
            it.powerWeight
        }
    }
     fun  readWeightConfig(): MutableList<LikeTrie.Entry<CardWeight>> {
         val weightData = mutableListOf<LikeTrie.Entry<CardWeight>>()

         weightData.add(LikeTrie.Entry(key = "1", value = CardWeight(4.0, 9.0, 0.0)))
         weightData.add(LikeTrie.Entry(key = "2", value = CardWeight(4.0,7.0,0.0)))
         weightData.add(LikeTrie.Entry(key = "3", value = CardWeight(4.1,8.0,0.0)))
         weightData.add(LikeTrie.Entry(key = "4", value = CardWeight(4.1,8.0,0.0)))
         weightData.add(LikeTrie.Entry(key = "3", value = CardWeight(2.0,7.0,0.0)))
         weightData.add(LikeTrie.Entry(key = "4", value = CardWeight(2.0,6.0,0.0)))
      return  weightData
    }


}


