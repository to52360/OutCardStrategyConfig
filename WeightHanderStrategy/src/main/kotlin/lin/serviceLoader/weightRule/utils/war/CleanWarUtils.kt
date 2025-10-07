package lin.serviceLoader.weightRule.utils.war


import lin.domain.WarInfo
import lin.domain.war.SimpleCleanWar

import lin.myLog
import lin.serviceLoader.weightRule.onWar.rival.ReleaseWar.Companion.ALL_CLEAN
import lin.serviceLoader.weightRule.utils.cardUtils.canHurtNum
import lin.serviceLoader.weightRule.utils.cardUtils.isExtWeight
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.twoLambda.findAtcSum
import lin.warExt.my.twoLambda.minionHasCanAttack
import lin.warExt.rival.findRivalAtcSum
import lin.warExt.rival.rivalCardsByPlayArea

/**
 * 省性能,但是没有考虑突袭和冲锋情况
 */
class CleanWarUtils(val warInfo: WarInfo) {
    companion object {
        const val CLEAN_KEY = "cleanWar"
        const val RELOAD_RIVAL_KEY = "reLoadRival"
    }

    var extRivalNum = 0
    var meCardsNun = 0

    //todo
    fun cleanOnce(damage: Int): Boolean {
        if (warInfo.roundExecuteOnce(CLEAN_KEY)) return false
        if (clean(damage)) return true
        reLoadMe()
        return false

    }

    fun rivalNumLessGap(warCardGap: Int): Boolean {
        if (!warInfo.roundExecuteOnce(RELOAD_RIVAL_KEY)) reload()
        return extRivalNum < warCardGap
    }

    private fun clean(damage: Int): Boolean {
        if (!warInfo.minionHasCanAttack()) return false //没有能够攻击的
        if (damage == ALL_CLEAN) {
            myLog.info { "清理全部" }
            warInfo.cleanPlayByRoundOnce()
        } else {
            val meCards = warInfo.playComboCards.filter { it.card.canHurt() }
            val lessDamageCards = meCards.filterTo(mutableListOf()) { it.card.blood() <= damage }
            if (lessDamageCards.isEmpty()) {
                warInfo.cleanPlayByRoundOnce()
            } else {
                lessDamageCards.removeIf { !it.card.canAttack() }
                if (lessDamageCards.isEmpty()) return false
                SimpleCleanWar(lessDamageCards, warInfo.war.rival).executeAttack()

            }
        }
        reload()
        return true
    }

    fun moreAtcThanGap(atcGap: Int): Boolean {
        val rivalAtc = warInfo.findRivalAtcSum() - warInfo.findAtcSum()
        return rivalAtc > atcGap
    }

    fun lessGap(warCardGap: Int, damage: Int): Boolean {
        var meCardsNun = this.meCardsNun
        if (extRivalNum > meCardsNun && damage != ALL_CLEAN) { //
            myLog.info { "敌方数量大于我方数量无视我方" }
            val meSurvive = warInfo.getPlayCards().count { it.canHurt() && it.blood() > damage }
            meCardsNun -= meSurvive
        }


        return extRivalNum - meCardsNun < warCardGap
    }

    fun reload() {
        reLoadRival()
        reLoadMe()

    }

    fun reLoadMe() {
        meCardsNun = warInfo.getPlayCards().canHurtNum()
    }

    fun reLoadRival() {
        val rivalCards = warInfo.rivalCardsByPlayArea()
        extRivalNum = rivalCards.canHurtNum()
        if (extRivalNum != 0 && rivalCards.isExtWeight()) extRivalNum++
    }


}