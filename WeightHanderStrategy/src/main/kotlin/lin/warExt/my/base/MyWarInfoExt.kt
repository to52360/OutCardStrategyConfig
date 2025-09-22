package lin.warExt.my.base


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo


fun WarInfo.getPlayCards(): List<Card> {
    return war.me.playArea.cards
}
fun WarInfo.getPlayCardSize(): Int {
    return getPlayCards().size
}
fun WarInfo.hasWeapon() = war.me.playArea.weapon != null


fun WarInfo.meBlood() = war.me.playArea.hero!!.blood()
fun WarInfo.getHandCards(): List<Card> {
    return war.me.handArea.cards
}
fun WarInfo.playCardIsFull(): Boolean {
    return getPlayCards().size == 7
}
fun WarInfo.getPower() = war.me.playArea.power

fun WarInfo.isPower(card: Card): Boolean = getPower() == card

fun WarInfo.getCost() = getNowCost() + extCost

fun WarInfo.getResource() = war.me.resources

fun WarInfo.getNowCost() = war.me.usableResource

fun WarInfo.hasCost() = getCost() > 0
