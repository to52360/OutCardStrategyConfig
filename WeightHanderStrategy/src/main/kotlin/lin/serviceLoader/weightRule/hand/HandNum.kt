package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition

/**
 * 手牌数量决定权重
 */
abstract class HandNum(val handSize: Int) : AbstractHandArea() {
    var unConditionWeight = NotWeight
    override fun setUnCondWeight(unConditionWeight: Double) {
        this.unConditionWeight = unConditionWeight
    }

    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        return if (handCards.size < handSize) {
            groupWeight
        } else {
            unConditionWeight
        }
    }
}

abstract class AbsHandNum(val filterList: (List<ComboCard>) -> List<ComboCard> = { it }) : AbsWeightCondition() {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val weight = filterList(warInfo.handComboCards).size * groupWeight
        if (callCard.powerWeight < NotWeight) {
            val useWeight = callCard.powerWeight + weight
            if (useWeight > NotWeight) return callCard.getExpectWeight(weight)
        }
        return weight
    }

}

class DefHandNum : AbsHandNum()