package lin.serviceLoader.weightRule.hand.drop

import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.AddWeightByWarInfo
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.PredicateByGroupId
import lin.warExt.base.getNowCost

class DropMin : AddWeightByWarInfo, DepByWeightGroupIdDelegate<DepToPredicate> by PredicateByGroupId() {
    /**
     * 通常都适应
     */
    override fun calculateWeight(warInfo: WarInfo): Double {
        val handCards = warInfo.handComboCards.sortedBy { it.cost() }
        var cost = warInfo.getNowCost()
        for (card in handCards) {
            if (card.cost() < 0) return -groupWeight
            if (depToPredicate(card)) return groupWeight
            if (card.useAble())//能够使用则减费用
                cost -= card.cost()
            else
                return -groupWeight
        }
        return -groupWeight
    }

    override var groupWeight: Double = CostWeight



}