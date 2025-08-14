package lin.warExt.rival

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum

import lin.domain.WarInfo

/**
 * 敌方场上随从攻击力总和
 */
fun WarInfo.rivalFindAtcSum(): Int {
    return war.rival.playArea.cards.sumOf { it.atc }
}
/**
 * 敌方攻击最高的随从
 */
fun WarInfo.rivalFindMaxAttackMinion(): Card? {
    return war.rival.playArea.cards
        .filter { it.cardType == CardTypeEnum.MINION }
        .maxByOrNull { it.atc }
}

fun WarInfo.rivalAllCardsByPlayArea() = war.rival.playArea.cards

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
fun WarInfo.rivalIsNotCardByPlayArea():Boolean{
    return rivalAllCardsByPlayArea().isEmpty()
}