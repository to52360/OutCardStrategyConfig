package lin.serviceLoader.weightRule.utils.cardUtils

import club.xiaojiawei.hsscriptcardsdk.bean.Card

/**
 * 存在额外价值
 */
fun List<Card>.isExtWeight(): Boolean {
    return this.any { it.isAura || it.isTriggerVisual || it.isTitan }
}

fun List<Card>.canHurt(): List<Card> {
    return this.filter { it.canHurt() }
}