package lin.serviceLoader.weightRule.onWar.buff

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.PredicateByRace

/**
 * 依赖种族统计
 */
class OnWarRaceNum : PredicateByRace() {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val playAreaCards = warInfo.playComboCards
        val unConditionWeight = unConditionWeight
        if (playAreaCards.isEmpty())
            return unConditionWeight
        var num = 0
        playAreaCards.forEach {
            if (predicateFromDep(it)) num++
        }
        return if (num == 0) unConditionWeight
        else num * groupWeight
    }
}