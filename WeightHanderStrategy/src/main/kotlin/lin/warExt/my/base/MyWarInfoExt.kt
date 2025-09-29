package lin.warExt.my.base


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.hand.HandArea
import lin.warExt.rival.rivalAllCardsByPlayArea
import kotlin.random.Random


fun WarInfo.getPlayCards(): List<Card> {
    return war.me.playArea.cards
}
fun WarInfo.getPlayCardSize(): Int {
    return getPlayCards().size
}
fun WarInfo.hasWeapon() = war.me.playArea.weapon != null

fun WarInfo.hasArmor(): Boolean {
    hero()?.let {
        return getArmor(it) > 0
    }
    return false
}

fun getArmor(card: Card?) = card?.armor ?: 0


fun WarInfo.hero() = war.me.playArea.hero

fun WarInfo.meBlood() = hero()!!.blood()
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

fun WarInfo.autoPower(card: Card): Boolean {
    val rivalCard = rivalAllCardsByPlayArea().randomSelectOrNull()
    var useResult = card.useCard(rivalCard)
    if (useResult) {
        return true
    }
    val meCard = getPlayCards().randomSelectOrNull()
    useResult = card.useCard(meCard)
    return useResult
}

fun Card.useCard(card: Card?): Boolean {
    return card?.let { this.area is HandArea && this.action.power(it) == null } ?: false
}

fun <T> List<T>.randomSelectOrNull(): T? {
    if (this.isEmpty()) return null
    return this[Random.nextInt(this.size)]
}
