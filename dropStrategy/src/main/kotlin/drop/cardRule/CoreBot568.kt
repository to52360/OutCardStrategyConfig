package  drop.cardRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.context.UseSkillWeight
import lin.serviceLoader.weightRule.CardRule
import lin.warExt.base.getCost
import lin.warExt.base.getHandCards

class CoreBot568 : CardRule {
    override fun cardId() = "CORE_BOT_568"

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val cardNum = warInfo.getHandCards().size
        if (cardNum < 4 || callCard.extPowerWeight != NotWeight) return NotWeight
        if (warInfo.getCost() < 3) {
            return callCard.getExpectWeight(UseSkillWeight + 1)
        }
        if (cardNum > 6) return UnUseWeight
        return NotWeight
    }

}