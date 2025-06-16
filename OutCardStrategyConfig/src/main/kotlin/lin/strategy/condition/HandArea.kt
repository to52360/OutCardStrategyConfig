package lin.strategy.condition

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.DBCard
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.util.CardDBUtil
import lin.bean.v2.ComboWeightInfo

/**
 *  [club.xiaojiawei.util.CardDBUtil]
 *  [DBCard]
 *  [club.xiaojiawei.bean.BaseCard]
 *  [club.xiaojiawei.enums.CardRaceEnum.fromString]
 *  []
 */
class HandArea<T> {
    val cache: Map<String,T> = emptyMap()
    var card: Card? = null
}

/**
 * 以种族作为跳跳
 */
class HandAreaByRace(comboWeightInfoList: Map<String,ComboWeightInfo>){

    private val cache: List<CardRaceEnum> = comboWeightInfoList.map{
        parse(it.key)
    }
    fun canUse(handCards: List<Card>): Boolean {
        return handCards.any{
            cache.any{cardRaceEnum -> it.cardRace == cardRaceEnum }
        }

    }
    private fun  parse(key:String): CardRaceEnum{
        CardDBUtil.queryCardById(key).let {
            if (it.isNotEmpty()) {
                return  CardRaceEnum.fromString(it.first().type)
            }else {
                return CardRaceEnum.UNKNOWN
            }

        }
    }
}
