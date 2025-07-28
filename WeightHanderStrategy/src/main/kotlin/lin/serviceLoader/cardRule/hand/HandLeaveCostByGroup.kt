package lin.serviceLoader.cardRule.hand

import lin.bean.ComboCard
import lin.serviceLoader.cardRule.utils.DepByWeightInfoDelegates
import lin.serviceLoader.cardRule.utils.DepToPredicates
import lin.serviceLoader.cardRule.utils.PredicateByGroup
import lin.weightHandler.condition.context.CostWeight
import lin.weightHandler.condition.context.DefaultWeight


/**
 * 存在足够费用使用配合牌,combo使用增加
 * 例如:玛克扎尔的小鬼+弃牌
 */
class HandLeaveCostByGroup : CanUseHandByLeaveCost,
    DepByWeightInfoDelegates<DepToPredicates> by PredicateByGroup() {
    override var groupWeight: Double = CostWeight



    override fun id()=25072601


    override fun onWarInfoProcessWeight(
        callCard: ComboCard,
        handCards: List<ComboCard>
    ) {
        val weight = if (depToPredicate(handCards)) groupWeight else DefaultWeight
        callCard.addWeight(weight)
    }
}

