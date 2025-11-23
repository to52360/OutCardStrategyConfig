package lin.serviceLoader.weightRule.utils.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.cardExt.cardList.canHurt
import lin.domain.WarInfo
import lin.lifecycle.RoundEnd
import lin.myLog
import lin.serviceLoader.weightRule.utils.war.WarStatus.Companion.ATTENTION_AVG_ATC
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.base.hero
import lin.warExt.my.base.meBlood
import lin.warExt.my.base.resource
import lin.warExt.rival.rivalCardsByPlayArea

/**
 * todo-future 准备迁移到上下文,就不用管理生命周期了
 * 用于判断战场整体局势
 *
 */
class WarStatus(val warInfo: WarInfo) : RoundEnd {
    companion object {
        const val ONCE_WAR_STATUS = "once"
        const val ATTENTION_AVG_ATC = 2
    }

    var rivalCards = emptyList<Card>()
    var rivalSumAtc = 0
    var meCards = emptyList<Card>()
    var meTaunt = emptyList<Card>()

    //用于判断缓存是否失效
    var meNum = 0
        private set

    //用于判断缓存是否失效
    var rivalNum = 0
        private set






    fun reLoadOnce(): Boolean {
        if (warInfo.roundExecuteOnce(ONCE_WAR_STATUS)) return false
        warInfo.registerLifecycle(this)
        reload()
        return true
    }

    override fun end(warInfo: WarInfo): Boolean {
        rivalCards = emptyList()
        meCards = emptyList()
        meTaunt = emptyList()
        return true
    }

    /**
     *
     */
    fun reload() {
        reloadMe()
        reloadRival()
        reloadStatus()
        myLog.info { "rivalSumAtc=$rivalSumAtc,meSumAtc=$meSumAtc,excessDamage=$excessDamage,meNum=$meNum" }
        myLog.info { "嘲讽血量:${meTaunt.sumOf { it.blood() }},能接受的攻击力:$ableAtcSum" }
    }

    /**
     * 节省性能,可能不是最新的
     */
    fun reloadByOption() {
        val isChange = reloadRivalByOption()
        if (reloadMeByOption() || isChange) {
            reloadStatus()
        }
    }


    fun reloadRival(rivalCards: List<Card> = warInfo.rivalCardsByPlayArea()) {
        rivalNum = rivalCards.size
        this.rivalCards = rivalCards.canHurt()
        rivalSumAtc = rivalCards.sumOf { it.atc }
    }

    fun reloadMe(meCards: List<Card> = warInfo.getPlayCards()) {
        meNum = meCards.size
        this.meCards = meCards.canHurt()
        this.meTaunt = meCards.filter { it.isTaunt }
    }

    /**
     * @return true 表示有变化
     */
    fun reloadMeByOption(): Boolean {
        val meCards = warInfo.getPlayCards()
        if (meCards.size != meNum) {
            reloadMe(meCards)
            return true
        }
        return false
    }
    /**
     * @return true 表示有变化
     */
    fun reloadRivalByOption(): Boolean {
        val rivalCards = warInfo.rivalCardsByPlayArea()
        if (rivalCards.size == rivalNum) {
            return false
        }
        // select 亡语会有出入

        myLog.info { "对手战场有变化重新加载" }
        reloadRival(rivalCards)
        return true

    }

    var meSumAtc = 0
        private set
    var excessDamage = 0
        private set
    var ableAtcSum = 0
        private set

    /**
     *
     */
    private fun reloadStatus() {
        meSumAtc = meCards.sumOf { it.atc }
        this.excessDamage = excessDamage()


        ableAtcSum = ableAtcSum()

    }

    private fun ableAtcSum(): Int {
        val ableAtcSum = acceptableRivalAttack(warInfo.resource())
        return warInfo.meBlood().coerceAtMost(warInfo.hero()!!.health) * ableAtcSum / warInfo.hero()!!.health

    }

}


const val ONE_FACTOR = 10

fun WarStatus.excessDamageFactor(): Int {
    if (ableAtcSum == 0) return excessDamage * ONE_FACTOR
    return excessDamage * ONE_FACTOR / ableAtcSum
}

fun WarStatus.overLimitByDamage(limit: Int = warInfo.meBlood() * 2): Boolean {
    return excessDamage - meSumAtc >= limit
}

fun WarStatus.excessDamageFactorByMeAtc(): Int {
    return (excessDamage - meSumAtc) * ONE_FACTOR / ableAtcSum
}

/**
 * todo-future 可以考虑英雄血量
 * 暂定方案
 * 还要考虑我方攻击力与动态攻击取最大值
 */
fun WarStatus.isAdvByMeAtc(ableAtcSum: Int = this.ableAtcSum): Boolean {
    val isAdv = isAdvByMeAtcLog(ableAtcSum)
    myLog.info { "是否有优势:$isAdv" }
    return isAdv

}

fun WarStatus.isAdvByMeAtcLog(ableAtcSum: Int = this.ableAtcSum): Boolean {

    //val meSumAtc = if(this.meSumAtc==0) 0 else this.meSumAtc/2
    if (excessDamage * 2 > warInfo.hero()!!.blood()) return false
    if (rivalSumAtc <= ableAtcSum) return true
    if (rivalSumAtc - meSumAtc <= ableAtcSum) return true
    if (excessDamage - meSumAtc <= ableAtcSum) return true
    return false
}

fun WarStatus.isAdv(ableAtcSum: Int = this.ableAtcSum): Boolean {
    return excessDamage < ableAtcSum
}

/**
 * @return false 平均攻击力表示超过
 */
fun WarStatus.compareAvgAct(avgAtc: Int = ATTENTION_AVG_ATC): Boolean {
    return rivalSumAtc < avgAtc * rivalCards.size
}

fun acceptableRivalAttack(mana: Int): Int {
    if (mana <= 0) return 0
    // 分段线性：低费用 1.5x+1，高费稍压低
    return when {
        mana <= 7 -> (1.5 * mana + 1).toInt()
        else -> (1.4 * mana + 1.2).toInt().coerceAtMost(15)
    }
}