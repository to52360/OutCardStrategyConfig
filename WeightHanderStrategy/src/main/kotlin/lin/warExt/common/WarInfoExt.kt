package lin.warExt.common

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum

import lin.domain.WarInfo

/**
 * ai辅助生成
 */

// ... existing code ...



/**
 * 我方场上随从攻击力总和
 */
fun WarInfo.findMeAtcSum(): Int {
    return war.me.playArea.cards.sumOf { it.atc }
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



