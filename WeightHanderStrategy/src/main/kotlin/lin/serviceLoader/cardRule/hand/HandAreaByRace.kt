package lin.serviceLoader.cardRule.hand

import lin.bean.ComboCard

import lin.serviceLoader.cardRule.utils.DepByWeightInfoDelegate
import lin.serviceLoader.cardRule.utils.PredicateByRace
import lin.serviceLoader.cardRule.utils.DepToPredicates
import lin.weightHandler.condition.context.DefaultWeight


import lin.weightHandler.condition.context.NotConditionDefaultWeight

/**
 * 以种族作为打出条件
 */
class HandAreaByRace : AbstractHandArea(),
    DepByWeightInfoDelegate<DepToPredicates> by PredicateByRace() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        return  if (depToPredicate(handCards)) DefaultWeight else groupWeight
    }
    override fun id(): Int = 250625013
}