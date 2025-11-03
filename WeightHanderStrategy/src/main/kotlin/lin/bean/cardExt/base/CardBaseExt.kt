package lin.bean.cardExt.base

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.CardType
import lin.bean.ComboCard

fun hasWorth(it: Card): Boolean {
    return it.isAura || it.isTriggerVisual || it.isTitan
}
fun ComboCard.isMinion() = card.isMinion()
fun Card.isMinion() = cardType == CardTypeEnum.MINION
fun ComboCard.atc() = card.atc


fun ComboCard.isCardType(cardType: CardType): Boolean {
    return cardWeightInfo?.isCardType(cardType) ?: false
}