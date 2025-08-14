package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeStrategy
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.AddWeightByAllInfo
import lin.warExt.base.getPlayCards

/**
 * 更改手牌
 */
class ChangeCardStrategy : AddWeightByAllInfo {


    override var groupWeight: Double = CostWeight
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        if (groupWeight != CostWeight) {//不是默认值,就增加查牌策略,,
            callCard.findStrategy = ChangeStrategy
        }
        if (warInfo.getPlayCards().size > 7) {
            return UnUseWeight
        }
        return NotWeight
    }

}