package lin.serviceLoader.weightRule.hand.race

import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.hand.AbstractHandArea
import lin.serviceLoader.weightRule.utils.DepToPredicateList
import lin.serviceLoader.weightRule.utils.DepWeightInfoDelegate
import lin.serviceLoader.weightRule.utils.PredicateListByRace

/**
 * 以种族作为打出条件
 */
class HandAreaRace : AbstractHandArea(),
    DepWeightInfoDelegate<DepToPredicateList> by PredicateListByRace() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        return if (depInfoToPredicate(handCards)) groupWeight else NotWeight
    }

    override fun description(): String {
        return "以手牌含有种族作为打出条件"
    }
}