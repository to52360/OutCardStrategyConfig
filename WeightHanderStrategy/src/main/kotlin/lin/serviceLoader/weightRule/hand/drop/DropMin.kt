package lin.serviceLoader.weightRule.hand.drop

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.hand.AbstractHandArea
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.PredicateByGroupId

class DropMin : AbstractHandArea(), DepByWeightGroupIdDelegate<DepToPredicate> by PredicateByGroupId() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        handCards.filter { !it.useAble() }.minByOrNull { it.getCost() }?.let {
            if (depToPredicate(it)) return groupWeight
        }
        return -groupWeight
    }

    override fun id() = 25080703
}