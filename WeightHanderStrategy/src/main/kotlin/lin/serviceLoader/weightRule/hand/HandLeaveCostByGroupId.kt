package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicates
import lin.serviceLoader.weightRule.utils.PredicateByGroupIds


/**
 * 存在足够费用使用配合牌,combo使用增加
 * 例如:玛克扎尔的小鬼+弃牌
 *
 */
class HandLeaveCostByGroupId : CanUseHandByLeaveCost,
    DepByWeightGroupIdDelegate<DepToPredicates> by PredicateByGroupIds() {
    override var groupWeight: Double = CostWeight


    override fun onLeaveCostProcessWeight(
        callCard: ComboCard,
        leaveCostHandCards: List<ComboCard>
    ): Double {
        return if (depToPredicate(leaveCostHandCards)) groupWeight else NotWeight
    }
}

