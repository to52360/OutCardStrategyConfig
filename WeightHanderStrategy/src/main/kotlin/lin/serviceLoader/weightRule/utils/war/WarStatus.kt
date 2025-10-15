package lin.serviceLoader.weightRule.utils.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo
import lin.lifecycle.RoundEnd
import lin.serviceLoader.weightRule.utils.cardUtils.canHurt
import lin.warExt.my.base.getPlayCards
import lin.warExt.rival.rivalCardsByPlayArea

class WarStatus(val warInfo: WarInfo) : RoundEnd {
    companion object {
        const val ONCE_WAR_STATUS = "once"
    }

    var rivalCards = emptyList<Card>()
    var meCards = emptyList<Card>()
    var sumAtc = 0
    var meTaunt = 0
    var rivalAvgAct = 0.0

    init {
        warInfo.registerLifecycle(this)
    }



    fun reLoadOnce(): Boolean {
        if (warInfo.roundExecuteOnce(ONCE_WAR_STATUS)) return false
        reload()
        return true
    }

    override fun end(warInfo: WarInfo) {
        rivalCards = emptyList()
        meCards = emptyList()
    }


    fun reload() {
        val rivalCards = warInfo.rivalCardsByPlayArea().canHurt()
        //select 亡语会有出入
        if (rivalCards.size != this.rivalCards.size) {
            this.rivalCards = rivalCards
            sumAtc = rivalCards.sumOf { it.atc }
            rivalAvgAct = if (rivalCards.isEmpty()) 0.0
            else sumAtc / rivalCards.size.toDouble()

        }

        reloadMe()

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
    fun isAdv(): Boolean {
        if (meCards.size >= rivalCards.size) return true
        if (meTaunt >= sumAtc) return true
        return false
    }

    fun isAdvByAvgAtc(avgAct: Int): Boolean {
        if (isAdv()) return true
        return rivalAvgAct > avgAct
    }
}