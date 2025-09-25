package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeGroupId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.combo.ComboParse.Companion.LastUseGroupId

import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight

import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getHandCards
import lin.warExt.my.base.playCardIsFull

/**
 * 更改手牌
 */
class ChangeCardStrategy : AbsWeightCondition() {
    override fun description(): String {
        return "groupWeight作为真正的权重,unConditionWeight和powerWeight用来判断数量"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {

        if (warInfo.getHandCards().size > 8) {
            if (warInfo.getCost() - callCard.cost() > 5 && !warInfo.playCardIsFull()) {
                val weight = parseAndCalWeight(callCard, warInfo, LastUseGroupId)
                return weight
            }
            return UnUseWeight
        } else {
            //用权重作为数量限制,避免写死,灵活但是书写更复杂
            val weight = parseAndCalWeight(callCard, warInfo, ChangeGroupId)
            return weight

        }
    }

    fun parseAndCalWeight(callCard: ComboCard, warInfo: WarInfo, useGroupId: Int): Double {
        val weight = calWeight(warInfo.getHandCards().size, callCard.powerWeight)
        if (weight > NotWeight) {
            callCard.useGroupId = useGroupId
            //修正权重,避免数量限制导致权重设置复杂
            return callCard.getExpectWeight(weight)
        }
        return weight
    }

    fun calWeight(unConditionSize: Int, baseWeight: Double): Double {
        val weight = unConditionSize * unConditionWeight + baseWeight
        return if (weight > NotWeight) {
            groupWeight
        } else {
            UnUseWeight
        }
    }


}
