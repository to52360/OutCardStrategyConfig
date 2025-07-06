package lin.weightHandler.condition.implCondition.util

import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.util.CardDBUtil
import lin.dao.ComboCard
import lin.weightHandler.condition.bean.ComboWeightInfo

interface WeightInfoGetRace {
    private fun parse(key: String): CardRaceEnum {
        CardDBUtil.queryCardById(key).let {
            if (it.isNotEmpty()) {
                return CardRaceEnum.fromString(it.first().type)
            } else {
                return CardRaceEnum.UNKNOWN
            }

        }
    }


    fun getCache(comboWeightInfoList: List<ComboWeightInfo>): List<CardRaceEnum> {
        var cache: List<CardRaceEnum> = emptyList()
        //select 空判断应该可以去掉,我前面已经判断过,这样的话要限制可见性
        if (comboWeightInfoList.isNotEmpty()) {
            cache = comboWeightInfoList.map {
                parse(it.cardId)
            }
        }
        return cache
    }



}
private fun ComboWeightInfo.parseRace(): CardRaceEnum =
    CardDBUtil.queryCardById(cardId).firstOrNull()?.type?.let(CardRaceEnum::fromString)?: CardRaceEnum.UNKNOWN
fun List<ComboWeightInfo>.toCardRaces(): List<CardRaceEnum> =
    mapNotNull { it.parseRace() }
// 策略生成器
fun List<CardRaceEnum>.createHandRacePredicate(): (List<ComboCard>) -> Boolean =
    { hand -> hand.any { card -> this.contains(card.card.cardRace) } }

fun List<CardRaceEnum>.createSingleRacePredicate(): (ComboCard) -> Boolean =
    { card -> this.contains(card.card.cardRace) }

/**
 * 对应种族存在手牌
 */
interface HandHasRace : WeightInfoGetRace {
    fun getFunction(comboWeightInfoList: List<ComboWeightInfo>): (List<ComboCard>) -> Boolean {
        val cacheRace = getCache(comboWeightInfoList)
        return { comboCards ->
            comboCards.any {
                cacheRace.any { cardRaceEnum -> it.card.cardRace == cardRaceEnum }
            }
        }
    }
}
interface ContainsRace : WeightInfoGetRace {
    fun getFunction(comboWeightInfoList: List<ComboWeightInfo>): (ComboCard) -> Boolean {
        val cacheRace = getCache(comboWeightInfoList)
        return { comboCard ->
            cacheRace.contains(comboCard.card.cardRace)
        }
    }
}