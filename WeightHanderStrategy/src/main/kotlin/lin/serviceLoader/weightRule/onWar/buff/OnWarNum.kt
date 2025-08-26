package lin.serviceLoader.weightRule.onWar.buff


import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.AddWeightByWarInfo
import lin.warExt.base.getPlayCards

class OnWarNum : AddWeightByWarInfo {
    override var groupWeight: Double = CostWeight
    override fun calculateWeight(warInfo: WarInfo): Double {
        return warInfo.getPlayCards().size * groupWeight
    }

    override fun description(): String {
        return "根据随从增加权重"
    }


}