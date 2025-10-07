package lin.serviceLoader.weightRule.onWar.rival

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.cardUtils.isExtWeight
import lin.warExt.rival.rivalCanHurt

class RivalNun : AbsWeightCondition() {
    override fun description(): String {
        return "对手数量加权"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val rivalCards = warInfo.rivalCanHurt()
        var extRivalNum = rivalCards.size
        if (extRivalNum != 0 && rivalCards.isExtWeight()) extRivalNum++
        return if (extRivalNum > number) groupWeight
        else unConditionWeight
    }


}