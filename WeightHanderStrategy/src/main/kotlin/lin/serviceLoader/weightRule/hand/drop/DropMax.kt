package lin.serviceLoader.weightRule.hand.drop

import lin.bean.ComboCard
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.hand.AbstractHandArea
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.PredicateByGroupId

class DropMax : AbstractHandArea(), DepByWeightGroupIdDelegate<DepToPredicate> by PredicateByGroupId() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        // ... existing code ...
        // 找出所有费用最高的牌
        val maxCost = handCards.maxOfOrNull { it.cost() } ?: return UnUseWeight
        val maxCostCards = handCards.filter { it.cost() == maxCost }
        // 计算满足条件的比例
        val validCount = maxCostCards.count { depToPredicate(it) }
        val ratio = validCount / maxCostCards.size

        // 返回比例乘以groupWeight
        return ratio * groupWeight
    }


}