package lin.serviceLoader.weightRule.utils.war


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo
import lin.domain.war.SimpleCleanWar
import lin.myLog
import lin.serviceLoader.weightRule.onWar.rival.CleanWar.Companion.ALL_CLEAN
import lin.serviceLoader.weightRule.utils.cardUtils.canHurt
import lin.serviceLoader.weightRule.utils.cardUtils.isExtWeight
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.base.meBlood
import lin.warExt.my.twoLambda.findMeTauntSumBlood
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

    lateinit var rivalCards: List<Card>
    private var extRivalNum = 0

    lateinit var meCards: List<Card>

    //todo
    fun cleanOnce(damage: Int): Boolean {
        if (warInfo.roundExecuteOnce(CLEAN_KEY)) return false
        if (clean(damage)) {
            myLog.info { "解场之前清理随从" }
            reload()
            return true
        }
        return false

    }

    fun rivalNumLessGap(warCardGap: Int): Boolean {
        reLoadOnce()
        val num = rivalCards.size + extRivalNum
        return num < warCardGap
    }

    /**
     * 每回合加载一次
     */
    fun reLoadOnce() {
        if (!warInfo.roundExecuteOnce(RELOAD_RIVAL_KEY)) reload()
    }

    private fun clean(damage: Int): Boolean {
        if (!warInfo.minionHasCanAttack()) return false //没有能够攻击的
        if (damage == ALL_CLEAN) {
            myLog.info { "战场清理全部" }
            warInfo.cleanPlayByRoundOnce()
        } else {
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


    fun lessGap(warCardGap: Int, damage: Int): Boolean {
        var meCardsNun = this.meCards.size
        val rivalNun = rivalCards.size + extRivalNum
        if (rivalNun > meCardsNun && damage != ALL_CLEAN) { //
            myLog.info { "敌方数量大于我方数量无视我方" }
            val meSurvive = warInfo.getPlayCards().count { it.canHurt() && it.blood() > damage }
            meCardsNun -= meSurvive
        }



        return rivalNun - meCardsNun < warCardGap
    }


    fun reload() {
        reLoadMe()
        reLoadRival()
    }

    /**
     * 无效清理率
     */
    fun unPassRate(damage: Int): Double {
        val unPassNum = rivalCards.count { it.blood() > damage }
        if (unPassNum == 0) return 0.0
        return unPassNum / rivalCards.size.toDouble()
    }

    fun reLoadMe() {
        meCards = warInfo.getPlayCards().canHurt()
    }

    fun reLoadRival() {
        val rivalCards = warInfo.rivalCardsByPlayArea()
        this.rivalCards = rivalCards.canHurt()
        extRivalNum()

    }

    private fun extRivalNum() {
        extRivalNum = 0
        if (this.rivalCards.isEmpty()) return
        val meTauntBlood = warInfo.findMeTauntSumBlood()
        if (rivalCards.isExtWeight()) extRivalNum++
        else if (meTauntBlood > 0) {
            val rivalAtcSum = rivalCards.sumOf { rivalCard ->
                if (rivalCard.canHurt()) rivalCard.atc
                0
            }
            //有嘲讽减数量
            if (meTauntBlood > rivalAtcSum) extRivalNum--
        }
        val meCardsNun = this.meCards.size
        val rivalNun = rivalCards.size
        if (rivalNun < meCardsNun) return

        val meBlood = warInfo.meBlood() + meTauntBlood
        val rivalAtc = warInfo.findRivalAtcSum()
        if (meBlood - rivalAtc < 10) extRivalNum++ //快没血就保守一点


    }

    /**
     * 单向清理,暂时放这一起
     */
    fun compareRivalNum(num: Int): Boolean {
        reLoadOnce()
        return num > rivalCards.size + extRivalNum

    }


}