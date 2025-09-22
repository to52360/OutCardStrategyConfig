package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition

/**
 * 手牌数量决定权重
 */
class HandNumWeight() : AbstractHandArea() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
        return handCards.size * groupWeight
    }
}

abstract class AbsHandNum(val filterList: (List<ComboCard>) -> List<ComboCard> = { it }) : AbsWeightCondition() {
    override fun description(): String {
        return "groupWeight作为真正的权重,unConditionWeight和powerWeight用来判断数量"
    }
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val weight = filterList(warInfo.handComboCards).size * unConditionWeight
        if (callCard.powerWeight < NotWeight) {
            val useWeight = callCard.powerWeight + weight
            if (useWeight > NotWeight) return callCard.getExpectWeight(groupWeight)
        }
        return weight
    }

}

class DefHandNum : AbsHandNum()