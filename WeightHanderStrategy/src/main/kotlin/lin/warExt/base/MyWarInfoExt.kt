package lin.warExt.base


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo


fun WarInfo.getPlayCards(): List<Card> {
    return war.me.playArea.cards
}

fun WarInfo.getHandCards(): List<Card> {
    return war.me.handArea.cards
}
fun WarInfo.playCardIsFull(): Boolean {
    return getPlayCards().size == 7
}

fun WarInfo.getNowCost() = war.me.usableResource

fun WarInfo.hasCost() = getNowCost() > 0
