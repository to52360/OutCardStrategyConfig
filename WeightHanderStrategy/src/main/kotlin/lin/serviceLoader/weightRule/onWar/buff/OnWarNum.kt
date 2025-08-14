package lin.serviceLoader.weightRule.onWar.buff

import lin.bean.ComboCard
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.onWar.OnWarInfo

class OnWarNum : OnWarInfo {
    override var groupWeight: Double = CostWeight
    override fun onPlayAreaCalcWeightByMe(playAreaCards: List<ComboCard>): Double {
        return playAreaCards.size * CostWeight
    }

    override fun description(): String {
        return "根据随从增加权重"
    }


}