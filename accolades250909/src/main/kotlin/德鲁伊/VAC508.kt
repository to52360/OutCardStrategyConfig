package 德鲁伊

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.PredicateByGroup

class VAC508 : PredicateByGroup() {
    override fun name(): String {
        return "能量零食"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val comboCard = warInfo.handComboCards.filter { predicateFromDep(it) }.minByOrNull { it.cost() }

        return unConditionWeight
    }
}