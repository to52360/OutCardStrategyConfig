package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.WeightCondition

/**
 * 手牌数量决定权重
 */
class HandNum(val handSize: Int) : AbstractHandArea() {
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