package lin.serviceLoader.weightRule.onWar

import lin.bean.ComboCard
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicates
import lin.serviceLoader.weightRule.utils.PredicateByGroupIds

class OnWarByGroupId : OnWarInfo, DepByWeightGroupIdDelegate<DepToPredicates> by PredicateByGroupIds() {
    override fun onPlayAreaCalcWeightByMe(playAreaCards: List<ComboCard>): Double {
        val weight = if (depToPredicate(playAreaCards)) groupWeight else NotWeight
        return weight
    }

    override fun description(): String {
        return "在战场有对应分组的卡"
    }

    override var groupWeight: Double = CostWeight

}