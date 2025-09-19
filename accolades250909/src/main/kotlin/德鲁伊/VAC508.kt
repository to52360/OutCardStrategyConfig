package 德鲁伊

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.PredicateByGroup
import lin.warExt.my.base.getResource

class VAC508 : PredicateByGroup() {
    override fun name(): String {
        return "能量零食"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val comboCard = warInfo.handComboCards.filter { predicateFromDep(it) }.minByOrNull { it.cost() }
        comboCard?.let {
            if (warInfo.getResource() + 3 >= it.cost())
                return groupWeight
        }

        return unConditionWeight
    }
}