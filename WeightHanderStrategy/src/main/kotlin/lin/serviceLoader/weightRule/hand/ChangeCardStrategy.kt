package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeGroupId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.combo.ComboParse.Companion.LastUseGroupId
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
        val handSize = warInfo.getHandCards().size
        if (handSize < number) {
            callCard.useGroupId = ChangeGroupId
            return groupWeight
        } else {
            if (warInfo.getHandCards().size > 8) {
                if (warInfo.getCost() - callCard.cost() > 5 && !warInfo.playCardIsFull()) { //可以打出
                    callCard.useGroupId = LastUseGroupId
                    return unConditionWeight
                }
                return UnUseWeight
            }
            callCard.useGroupId = ChangeGroupId
            return unConditionWeight
        }


    }


}
