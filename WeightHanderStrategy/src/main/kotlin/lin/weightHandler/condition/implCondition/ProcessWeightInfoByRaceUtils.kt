package lin.weightHandler.condition.implCondition


import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.util.CardDBUtil
import lin.bean.ComboCard
import lin.myLog
import lin.bean.CardWeightInfo

//select 多种实现要转化为接口
fun CardWeightInfo.parseRace(): CardRaceEnum =
    CardDBUtil.queryCardById(cardId).firstOrNull()?.type?.let(CardRaceEnum::fromString)?:run{
        //select 实际运行后看一下null怎么处理
        myLog.warn { "卡牌:${cardId}没有种族信息" }
        CardRaceEnum.UNKNOWN
    }
fun List<CardWeightInfo>.toCardRaces(): List<CardRaceEnum> =
    mapNotNull { it.parseRace() }

// 策略生成器
fun List<CardRaceEnum>.createHandRacePredicate(): (List<ComboCard>) -> Boolean =
    { hand -> hand.any { card -> this.contains(card.card.cardRace) } }

fun List<CardRaceEnum>.createSingleRacePredicate(): (ComboCard) -> Boolean =
    { card -> this.contains(card.card.cardRace) }

//select 如果太多再拆分
//组合
fun List<CardWeightInfo>.infoToHandRacePredicate(): (List<ComboCard>) -> Boolean = this.toCardRaces().createHandRacePredicate()



