package lin.serviceLoader.weightRule.onWar

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.twoLambda.findMeTauntSumBloodByPlayArea
import lin.warExt.rival.rivalAllCardsByPlayArea
import lin.warExt.rival.rivalBlood
import lin.warExt.rival.rivalFindAtcSum

class ReleaseWar : AbsWeightCondition() {
    override fun name(): String {
        return "解场用的"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val rivalCards = warInfo.rivalAllCardsByPlayArea()
        val meCards = warInfo.getPlayCards()
        if (rivalCards.size - meCards.size > 3) {
            return groupWeight
        }

        //场攻大于12
        val rivalAtc = warInfo.rivalFindAtcSum()
        if (rivalAtc > 12) return groupWeight

        //快没血
        val meBlood = warInfo.rivalBlood() + warInfo.findMeTauntSumBloodByPlayArea()
        if (meBlood - rivalAtc < 10) return groupWeight
        return unConditionWeight
    }
}