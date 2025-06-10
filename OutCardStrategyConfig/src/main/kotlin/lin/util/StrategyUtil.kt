package lin.util

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.SimulateWeightCard
import club.xiaojiawei.config.log
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import lin.bean.WeightCard
import lin.bean.v1.ComboWeightInfo
import java.io.IOException
import java.nio.file.Path
import kotlin.collections.ArrayList

object StrategyUtil {
    fun sortCardByPowerWeight(cards: List<SimulateWeightCard>): List<SimulateWeightCard> {
        return cards.sortedByDescending{
            it.powerWeight = CARD_WEIGHT_TRIE[it.card.cardId]?.powerWeight ?: 1.0
            it.powerWeight
        }
    }
    class Entry(
        var key: String,
        var value: CardWeight,
    )
     fun  readWeightConfig(): MutableList<Entry> {
         val weightData = mutableListOf<Entry>()
         weightData.add(Entry(key = "1", value = CardWeight(4.0,9.0,0.0)))
         weightData.add(Entry(key = "2", value = CardWeight(4.0,7.0,0.0)))
         weightData.add(Entry(key = "3", value = CardWeight(4.1,8.0,0.0)))
         weightData.add(Entry(key = "4", value = CardWeight(4.1,8.0,0.0)))
         weightData.add(Entry(key = "3", value = CardWeight(2.0,7.0,0.0)))
         weightData.add(Entry(key = "4", value = CardWeight(2.0,6.0,0.0)))
      return  ArrayList()
    }


}


