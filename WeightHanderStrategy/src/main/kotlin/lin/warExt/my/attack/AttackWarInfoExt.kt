package lin.warExt.my.attack

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard

import lin.domain.WarInfo
import lin.warExt.rival.rivalBlood

/**
 * ai辅助生成
 */

// ... existing code ...



/**
 * 我方场上随从攻击力总和
 */
fun WarInfo.findMeAtcSumByCanAttack(cards: List<ComboCard> = findMeAtc()): Int {
    return cards.sumOf { it.card.atc }
}

inline fun WarInfo.findMeAtc(predicate: (ComboCard) -> Boolean = { it.card.canAttack() }): List<ComboCard> {
    return playComboCards.filter { predicate(it) }
}

/**
 * 攻击之后剩余血量
 */
fun WarInfo.attackAfterLessBlood(cards: List<ComboCard> = findMeAtc()): Int {
    return rivalBlood() - findMeAtcSumByCanAttack(cards)
}



/**
 * 我方攻击最高的随从
 */
fun WarInfo.findMeMaxAttackMinion(): Card? {
    return war.me.playArea.cards
        .filter { it.cardType == CardTypeEnum.MINION }
        .maxByOrNull { it.atc }
}


fun WarInfo.findMePlayAreaNum(): Int {
    return war.me.playArea.cards.size
}
fun WarInfo.hasTaunt():Boolean{
    return war.me.playArea.cards.any{
         it.isTaunt }
}


/**
 * 墓地牌
 */
fun WarInfo.getGraveyardCards() = war.me.graveyardArea.cards

inline fun WarInfo.getGraveyardCards(filter: (Card) -> Boolean): List<Card> {
    return getGraveyardCards().filter(filter)
}

fun WarInfo.getGraveyardCardsByType(cardTypeEnum: CardTypeEnum): List<Card> {
    return getGraveyardCards { card -> card.cardType == cardTypeEnum }

}



