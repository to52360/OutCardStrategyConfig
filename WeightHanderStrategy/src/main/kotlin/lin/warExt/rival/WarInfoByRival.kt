package lin.warExt.rival

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.cardExt.cardList.canHurt
import lin.bean.cardExt.cardList.canHurtNum
import lin.domain.WarInfo

/**
 * 敌方场上随从攻击力总和
 */
fun WarInfo.findRivalAtcSum(): Int {
    return rivalCardsByPlayArea().sumOf { it.atc }
}
/**
 * 敌方攻击最高的随从
 */
fun WarInfo.rivalFindMaxAtcMinion(): Card? {
    return rivalCardsByPlayArea()
        .filter { it.cardType == CardTypeEnum.MINION }
        .maxByOrNull { it.atc }
}

fun WarInfo.rivalCardsByPlayArea() = war.rival.playArea.cards

fun WarInfo.rivalCanHurt() = rivalCardsByPlayArea().canHurt()
fun WarInfo.rivalCanHurtCount() = rivalCardsByPlayArea().canHurtNum()
fun WarInfo.rivalSecretSize() = war.rival.secretArea.cards.size

fun WarInfo.rivalHandSize() = war.rival.handArea.cards.size

/**
 * 敌方生命值最少的卡牌
 */
fun WarInfo.rivalFindMinHealthCard(): Card? {
    return war.rival.playArea.cards.minByOrNull { it.health }
}
fun WarInfo.rivalPlayAreaSize(): Int{
    return war.rival.playArea.cards.size
}
fun WarInfo.rivalNotHasMinion(): Boolean {
    val cards = rivalCardsByPlayArea()
    return cards.isEmpty() || cards.count { it.canHurt() } == 0
}

fun WarInfo.rivalBlood() = war.rival.playArea.hero!!.blood()