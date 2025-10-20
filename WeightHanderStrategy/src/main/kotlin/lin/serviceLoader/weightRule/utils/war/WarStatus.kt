package lin.serviceLoader.weightRule.utils.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo
import lin.lifecycle.RoundEnd
import lin.myLog
import lin.serviceLoader.weightRule.utils.cardUtils.canHurt
import lin.serviceLoader.weightRule.utils.war.WarStatus.Companion.ATTENTION_AVG_ATC
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.base.resource
import lin.warExt.rival.rivalCardsByPlayArea

class WarStatus(val warInfo: WarInfo) : RoundEnd {
    companion object {
        const val ONCE_WAR_STATUS = "once"
        const val ATTENTION_AVG_ATC = 2
    }

    var rivalCards = emptyList<Card>()
    var rivalSumAtc = 0
    var meCards = emptyList<Card>()
    var meTaunt = emptyList<Card>()

    var meNum = 0
        private set
    var rivalNum = 0
        private set
    var isAdv = true
        private set





    fun reLoadOnce(): Boolean {
        if (warInfo.roundExecuteOnce(ONCE_WAR_STATUS)) return false
        warInfo.registerLifecycle(this)
        reload()
        return true
    }

    override fun end(warInfo: WarInfo) {
        rivalCards = emptyList()
        meCards = emptyList()
        meTaunt = emptyList()
        //todo-future 不知道是否需要一直存在
        warInfo.logoutLifecycle(this)
    }

    fun reload() {
        reloadMe()
        reloadRival()
    }

    /**
     * 节省性能,可能出错
     */
    fun reloadByOption() {
        val rivalCards = warInfo.rivalCardsByPlayArea()
        var isUpdate = false
        //select 亡语会有出入
        if (rivalCards.size != rivalNum) {
            myLog.info { "对手战场有变化重新加载" }
            reloadRival(rivalCards)
            isUpdate = true
        }
        if (isUpdate || reloadMeByOption()) {
            myLog.info { "有变化重新加载是否有优势,现在是否有优势:${isAdv}" }
            isAdv = isAdvByComplex()
            myLog.info { "执行之后,isAdv=${isAdv}" }

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
     * @return true
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
     * 有优势,复杂策略.没考虑血量问题
     * @param ableAtcSum 可接受场攻是多少
     */
    fun isAdvByComplex(ableAtcSum: Int = acceptableRivalAttack(warInfo.resource())): Boolean {
        //todo 没考虑随从数量

        val rivalAtcSum = rivalCards.sumOf { it.atc }
        //快速结束我方有优势
        if (rivalAtcSum <= ableAtcSum) return true


        val excessDamage = excessDamageByTaunt(rivalAtcSum)



        if (excessDamage <= ableAtcSum) return true

        //场攻击大于可接收范围,进一步判断
        if (meTaunt.isEmpty()) return false

        val tauntNum = meTaunt.size

        val rivalNum = rivalCards.size


        // 1. 如果嘲讽数量 ≥ 敌方数量：全部攻击被挡
        if (tauntNum >= rivalNum) {

            return true
        }

        val sortedRivalAtc = rivalCards.map { it.atc }.sortedDescending()
        val attackingTauntAtc = sortedRivalAtc.take(tauntNum).sum()      // 打嘲讽的总攻
        val directDamage = sortedRivalAtc.drop(tauntNum).sum()           // 打脸的溢出伤害

        val canBlock = meTauntBlood >= attackingTauntAtc
        val acceptableDirect = directDamage <= ableAtcSum
        return canBlock && acceptableDirect

    }

}

fun WarStatus.excessDamageByTaunt(rivalAtcSum: Int): Int {
    if (meTaunt.isEmpty()) return rivalAtcSum
    val meTauntBlood = meTaunt.sumOf { it.blood() }
    if (meTauntBlood >= rivalSumAtc) return 0

    val tauntNum = meTaunt.size

    val rivalNum = rivalCards.size


    // 1. 如果嘲讽数量 ≥ 敌方数量：全部攻击被挡
    if (tauntNum >= rivalNum) {
        return 0
    }
    return rivalAtcSum - meTauntBlood
}

/**
 * 实验性算溢出伤害,暂时方案
 * select ai生成 有点问题,只考虑到攻击力最高攻击情况
 */
fun WarStatus.excessDamageByAi(): Int {
    //  2. 嘲讽数量 < 敌方数量：部分攻击会溢出
    val sortedRivalAtc = rivalCards.map { it.atc }.sortedDescending()
    val directDamage = sortedRivalAtc.drop(meTaunt.size).sum()

    return directDamage
}


fun WarStatus.compareRivalMeGap(numGap: Int, ableAtcSum: Int = acceptableRivalAttack(warInfo.resource())): Boolean {

    TODO()
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