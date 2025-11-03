package lin.bean.cardExt.cardList

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.cardExt.base.hasWorth

/**
 * 存在额外价值
 */
fun List<Card>.isExtWeight(): Boolean {
    return this.any { hasWorth(it) }
}

fun List<Card>.canHurt(): List<Card> {
    return this.filter { it.canHurt() }
}
fun List<Card>.canHurtNum(): Int {
    return this.count { it.canHurt() }
}
