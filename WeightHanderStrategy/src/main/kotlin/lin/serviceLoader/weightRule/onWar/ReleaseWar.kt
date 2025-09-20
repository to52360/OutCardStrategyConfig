package lin.serviceLoader.weightRule.onWar

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.combo.FirstUseCombo.Companion.FirstUseGroupId
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.twoLambda.findMeTauntSumBloodByPlayArea
import lin.warExt.rival.rivalAllCardsByPlayArea
import lin.warExt.rival.rivalBlood
import lin.warExt.rival.rivalFindAtcSum

/**
 * todo 数量可以用权重解决
 */
abstract class ReleaseWar(val warCardGap: Int, val rivalAtc: Int, val lessBlood: Int) : AbsWeightCondition() {
    override fun name(): String {
        return "解场用的之aoe"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val weight = calWeight(warInfo)
        if (weight > NotWeight) {
            callCard.useGroupId = FirstUseGroupId
            return weight
        }
        return UnUseWeight
    }

    private fun calWeight(warInfo: WarInfo): Double {
        val rivalCards = warInfo.rivalAllCardsByPlayArea()
        val meCards = warInfo.getPlayCards()

        if (rivalCards.size - meCards.size >= this.warCardGap) {
            return groupWeight
        }
        val unWeight = meCards.size * unConditionWeight
        //场攻大于12
        val rivalAtc = warInfo.rivalFindAtcSum()
        if (rivalAtc > this.rivalAtc) return groupWeight + unWeight

        //快没血
        val meBlood = warInfo.rivalBlood() + warInfo.findMeTauntSumBloodByPlayArea()
        if (meBlood - rivalAtc < lessBlood) return groupWeight + unWeight
        return UnUseWeight
    }
    /*    private fun calWeight1( warInfo: WarInfo): Double{
            val rivalCards = warInfo.rivalAllCardsByPlayArea()
            val meCards = warInfo.getPlayCards()
            val unWeight = meCards.size*unConditionWeight
            if (rivalCards.size - meCards.size >= this.warCardGap) {
                return groupWeight*rivalCards.size + unWeight
            }

            //场攻大于12
            val rivalAtc = warInfo.rivalFindAtcSum()
            if (rivalAtc > this.rivalAtc) return groupWeight + unWeight

            //快没血
            val meBlood = warInfo.rivalBlood() + warInfo.findMeTauntSumBloodByPlayArea()
            if (meBlood - rivalAtc < lessBlood) return groupWeight + unWeight
            return UnUseWeight
        }*/
}

class MaxReleaseWar() : ReleaseWar(4, 12, 10)
class MinReleaseWar() : ReleaseWar(2, 5, 10)

