package lin.serviceLoader.weightRule.status

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getCost

class CostWeightByNum : AbsWeightCondition() {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return if (warInfo.getCost() - callCard.cost() > number) groupWeight
        else unConditionWeight
    }
}