package lin.warExt.base

import club.xiaojiawei.bean.Card
import lin.domain.WarInfo
import lin.warExt.base.getNowCost


fun WarInfo.getPlayCards(): List<Card> {
    return war.me.playArea.cards
}

fun WarInfo.getHandCards(): List<Card> {
    return war.me.handArea.cards
}

fun WarInfo.getNowCost() = war.me.usableResource

fun WarInfo.hasCost() = getNowCost() > 0
