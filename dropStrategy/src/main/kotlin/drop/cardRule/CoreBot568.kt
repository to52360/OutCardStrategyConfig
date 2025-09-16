package  drop.cardRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.CardRule
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getHandCards

class CoreBot568 : CardRule {
    override fun cardId() = "CORE_BOT_568"

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val cardNum = warInfo.getHandCards().size
        if ((cardNum < 4 && warInfo.getCost() > 2) ||
            //说明有配合
            callCard.extPowerWeight != NotWeight
        ) return NotWeight
        if (cardNum > 6 || warInfo.getCost() < 3) return UnUseWeight
        return NotWeight
    }

}