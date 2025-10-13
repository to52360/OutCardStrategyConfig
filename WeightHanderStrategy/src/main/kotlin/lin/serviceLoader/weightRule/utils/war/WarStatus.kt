package lin.serviceLoader.weightRule.utils.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.cardUtils.canHurt
import lin.warExt.my.base.getPlayCards
import lin.warExt.rival.rivalCardsByPlayArea

class WarStatus(val warInfo: WarInfo) {
    companion object {
        const val ONCE_WAR_STATUS = "once"
    }

    var rivalCards = emptyList<Card>()
    var meCards = emptyList<Card>()
    var sumAtc = 0
    var meTaunt = 0
    var rivalAvgAct = 0.0


    fun overAvgAtcByRival(avgAct: Int): Boolean {
        return rivalAvgAct > avgAct
    }

    fun reLoadOnce(): Boolean {
        if (warInfo.roundExecuteOnce(ONCE_WAR_STATUS)) return false
        reload()
        return true
    }

    fun reload() {
        rivalCards = warInfo.rivalCardsByPlayArea().canHurt()
        sumAtc = rivalCards.sumOf { it.atc }
        rivalAvgAct = if (rivalCards.isEmpty()) 0.0
        else sumAtc / rivalCards.size.toDouble()
        reloadMe()
    }

    fun reloadMe() {
        meCards = warInfo.getPlayCards().canHurt()
        meTaunt = meCards.filter { it.isTaunt }.sumOf { it.blood() }

    }

    /**
     * 有优势
     * todo 暂定 初步方案
     */
    fun isAdv(avgAct: Int): Boolean {
        if (meTaunt >= sumAtc) return true
        if (rivalAvgAct < avgAct) return true

        return false
    }
}