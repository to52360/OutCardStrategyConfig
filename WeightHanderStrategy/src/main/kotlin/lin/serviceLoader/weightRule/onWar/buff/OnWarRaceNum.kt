package lin.serviceLoader.weightRule.onWar.buff

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.onWar.OnWarInfo
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.DepWeightInfoDelegate
import lin.serviceLoader.weightRule.utils.PredicateOneByRace
import lin.serviceLoader.weightRule.utils.abs.PredicateByRace

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