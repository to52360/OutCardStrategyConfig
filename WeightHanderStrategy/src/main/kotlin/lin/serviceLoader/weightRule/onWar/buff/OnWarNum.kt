package lin.serviceLoader.weightRule.onWar.buff

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.onWar.OnWarInfo
import lin.weightHandler.condition.context.CostWeight

class OnWarNum : OnWarInfo {
    override var groupWeight: Double = CostWeight
    override fun onPlayAreaCalcWeightByMe(playAreaCards: List<ComboCard>): Double {
        return playAreaCards.size * CostWeight
    }

    override fun description(): String {
        return "根据随从增加权重"
    }

    override fun id() = 25080701

}