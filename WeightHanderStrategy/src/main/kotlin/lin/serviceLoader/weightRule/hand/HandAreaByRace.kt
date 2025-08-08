package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.utils.DepByWeightInfoDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicates
import lin.serviceLoader.weightRule.utils.PredicateByRace
import lin.weightHandler.condition.context.NotWeight


/**
 * 以种族作为打出条件
 */
class HandAreaByRace : AbstractHandArea(),
    DepByWeightInfoDelegate<DepToPredicates> by PredicateByRace() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        return  if (depToPredicate(handCards)) groupWeight else NotWeight
    }

    override fun description(): String {
        return "以手牌含有种族作为打出条件"
    }
}