@file:JvmName("CleanWarUtils11Kt")

package lin.serviceLoader.weightRule.utils.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.cardExt.base.hasWorth
import lin.domain.WarInfo
import lin.domain.war.SimpleCleanWar
import lin.lifecycle.RoundEnd
import lin.myLog
import lin.serviceLoader.weightRule.onWar.rival.CleanWar.Companion.ALL_CLEAN
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.twoLambda.minionHasCanAttack

/**
 *
 */
class CleanWarUtils(val warInfo: WarInfo) : RoundEnd {
    companion object {
        const val CLEAN_KEY = "cleanWar"
        const val RELOAD_RIVAL_KEY = "reLoadRival"

    }

    //使用lazy解决循环依赖问题,ui加载报错问题
    val warStatus: WarStatus by lazy {
        warInfo.warStatus
    }

    //关键目标,全局光环会影响判断(代求证)
    var worthTarget: List<Card> = emptyList()
    /**
     * 辅助回收垃圾
     */
    override fun end(warInfo: WarInfo): Boolean {
        worthTarget = emptyList()
        return true
    }
    fun
            cleanOnce(damage: Int): Boolean {
        if (warInfo.roundExecuteOnce(CLEAN_KEY)) return false
        if (clean(damage)) {
            myLog.info { "解场之前清理随从" }
            reload()
            return true
        }
        return false
    }
    private fun clean(damage: Int): Boolean {
        if (!warInfo.minionHasCanAttack()) return false //没有能够攻击的
        if (damage == ALL_CLEAN || warInfo.getPlayCards().any { it.isPoisonous }) {//没有适配,剧毒用通用的
            myLog.info { "战场清理全部" }
            warInfo.cleanPlayByRoundOnce()
        } else {
            //没有考虑剧毒问题
            val meCards = warInfo.playComboCards.filter { it.card.canHurt() }
            val lessDamageCards = meCards.filterTo(mutableListOf()) { it.card.blood() <= damage }
            if (lessDamageCards.isEmpty()) {
                return warInfo.cleanPlayByRoundOnce()
            } else {
                lessDamageCards.removeIf { !it.card.canAttack() }
                if (lessDamageCards.isEmpty()) return false
                return SimpleCleanWar(lessDamageCards, warInfo.war.rival).executeAttack()

            }
        }
        return true
    }

    /**
     * 每回合加载一次
     */
    fun reLoadOnce(): Boolean {
        if (warInfo.roundExecuteOnce(RELOAD_RIVAL_KEY)) return false
        reload()
        return true
    }

    fun reload() {
        warStatus.reLoadOnce()
        //
        worthTarget = warStatus.rivalCards.filter { hasWorth(it) && it.cost > 1 }
    }

    fun rivalNumLessGap(warCardGap: Int): Boolean {
        reLoadOnce()
        return warStatus.rivalNum < warCardGap

    }
    fun lessGap(warCardGap: Int, damage: Int): Boolean {


        var meCardsNun = warStatus.meCards.size
        val rivalNun = warStatus.rivalCards.size
        if (rivalNun > meCardsNun && damage != ALL_CLEAN) { //
            myLog.info { "敌方数量大于我方数量无视我方" }
            val meSurvive = warInfo.getPlayCards().count { it.canHurt() && it.blood() > damage }
            meCardsNun -= meSurvive
        }

        return rivalNun - meCardsNun < warCardGap
    }

    /**
     * 单向清理,暂时放这一起
     */
    fun compareRivalNum(num: Int): Boolean {
        reLoadOnce()
        return num > warStatus.rivalCards.size

    }

    /**
     * 有清理价值
     */
    fun hasCleanWorth(): Boolean {
        if (hasWorthTarget()) return true
        if (!warStatus.isAdv()) return true
        return false

    }

}

/**
 * 无效清理率
 */
fun CleanWarUtils.unPassRate(damage: Int): Double {
    val rivalCards = warStatus.rivalCards
    val unPassNum = rivalCards.count { it.blood() > damage }
    if (unPassNum == 0) return 0.0
    return unPassNum.toDouble() / rivalCards.size
}

fun CleanWarUtils.hasWorthTarget(): Boolean {
    return worthTarget.isNotEmpty()
}

fun CleanWarUtils.hasWorthReduceNum(warCardGap: Int): Int {
    if (worthTarget.isNotEmpty()) return warCardGap - 1
    return warCardGap
}

//用于使用合适伤害,清理卡牌的修正权重
fun CleanWarUtils.getExcessDamageFixWeight(damage: Int): Double {
    var fixWeight: Double
    val excessDamageWeight = 0.01 //避免清理威力更大的清场牌的修正权重
    val unCleanWeight = 0.1
    val maxBlood = warStatus.rivalCards.maxOf { it.blood() }
    if (damage > maxBlood) {
        fixWeight = excessDamageWeight * (damage - maxBlood)
        if (fixWeight > 0.1) fixWeight = 0.1
    } else {
        fixWeight = (maxBlood - damage) * unCleanWeight
        if (fixWeight > 1) fixWeight = 1.0
    }
    return -fixWeight
}

fun CleanWarUtils.isHasAvg(avg: Int = 2): Boolean {
    return avg * warStatus.rivalCards.size < warStatus.rivalCards.sumOf { it.atc }
}