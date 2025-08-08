package lin.serviceLoader.weightRule.hand.drop

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.hand.AbstractHandArea
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.PredicateByGroupId
import lin.weightHandler.condition.context.UnUseWeight

class DropMax : AbstractHandArea(), DepByWeightGroupIdDelegate<DepToPredicate> by PredicateByGroupId() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        handCards.maxByOrNull { it.getCost() }?.let {
            if (depToPredicate(it)) return groupWeight
        }
        return UnUseWeight
    }


}