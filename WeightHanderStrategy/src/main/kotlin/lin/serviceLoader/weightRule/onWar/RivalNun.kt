package lin.serviceLoader.weightRule.onWar

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.rival.rivalAllCardsByPlayArea

class RivalNun : AbsWeightCondition() {
    override fun description(): String {
        return "对手数量加权"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return warInfo.rivalAllCardsByPlayArea().size * groupWeight
    }

}