package lin.warExt.my.twoLambda

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo
import lin.warExt.my.base.getPlayCards



//两/三层函数嵌套

inline fun WarInfo.findMeByPlayArea(predicate: (Card) -> Boolean): List<Card> {
    return getPlayCards().filter { it -> predicate(it) }
}

fun WarInfo.findMeTauntByPlayArea(): List<Card> {
    return findMeByPlayArea { it.isTaunt }
}

fun WarInfo.findMeTauntSumBlood(): Int {
    return findMeTauntByPlayArea().sumOf { it.blood() }
}

fun WarInfo.findAtcSum(): Int {
    return getPlayCards().sumOf { it.atc }
}

fun WarInfo.hasCanAttack() = getPlayCards().any { it.canAttack() }