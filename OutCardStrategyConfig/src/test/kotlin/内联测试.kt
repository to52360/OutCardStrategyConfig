import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.util.CardDBUtil
import lin.dao.ComboCard
import lin.weightHandler.condition.bean.ComboWeightInfo

class 内联测试 {
}


 fun parse(key: String): CardRaceEnum {
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
fun getFunction(comboWeightInfoList: List<ComboWeightInfo>): (List<ComboCard>) -> Boolean {
    val cacheRace = getCache(comboWeightInfoList)
    return { comboCards ->
        comboCards.any {
            cacheRace.any { cardRaceEnum -> it.card.cardRace == cardRaceEnum }
        }
    }
}