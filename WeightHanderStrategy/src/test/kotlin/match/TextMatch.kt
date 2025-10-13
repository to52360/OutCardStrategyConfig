package match

import lin.serviceLoader.weightRule.onWar.rival.utils.extractNumber
import kotlin.test.Test

class TextMatch {
    @Test
    fun test1() {
        val text = "对所有随从造成1点伤害，造成三次。如果你在上个回合施放过法术，则法力值消耗减少（2）点。"
        val damage = extractNumber(text)
        println(damage)

    }
}