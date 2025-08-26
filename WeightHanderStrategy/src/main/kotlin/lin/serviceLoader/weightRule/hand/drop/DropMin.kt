package lin.serviceLoader.weightRule.hand.drop

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.WeightCondition
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.PredicateByGroupId
import lin.warExt.base.getNowCost

class DropMin : WeightCondition, DepByWeightGroupIdDelegate<DepToPredicate> by PredicateByGroupId() {
    /**
     * 通常都适应
     */
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val handCards = warInfo.handComboCards.sortedBy { it.cost() }
        var cost = warInfo.getNowCost() - callCard.cost()
        for (card in handCards) {
            if (callCard == card) continue
            //最小值符合要求
            if (depToPredicate(card)) return groupWeight

            //假设都使用完费用,还是不满足条件
            if (cost <= 0) return -groupWeight

            //能够使用则减费用,不能使用就是最小值
            if (card.useAble())
                cost -= card.cost()
            else
                return -groupWeight
        }
        return -groupWeight
    }

    override var groupWeight: Double = CostWeight



}