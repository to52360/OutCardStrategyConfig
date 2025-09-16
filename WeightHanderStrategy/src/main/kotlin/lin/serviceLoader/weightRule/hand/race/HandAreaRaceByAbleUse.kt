package lin.serviceLoader.weightRule.hand.race

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.OrderWeight
import lin.serviceLoader.weightRule.utils.abs.PredicateByRace
import lin.warExt.my.base.getCost

/**
 * 种族光环
 */
class HandAreaRaceByAbleUse : PredicateByRace() {

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        var num = 0
        var ableCost = warInfo.getCost() - callCard.cost()
        if (ableCost < 0) return unConditionWeight
        val filters = warInfo.canUseCards.filter {
            predicateFromDep(it)
        }.sortedBy { it.cost() }
        for (comboCard in filters) {
            ableCost -= comboCard.cost()
            if (ableCost < 0) break
            num++
        }

        if (num == 0) return unConditionWeight

        filters.forEach { //对应种族加权
            it.addWeight(groupWeight - OrderWeight)
        }
        return groupWeight * num

    }


}