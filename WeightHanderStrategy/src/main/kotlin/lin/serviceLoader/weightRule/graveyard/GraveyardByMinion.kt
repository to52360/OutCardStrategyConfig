package lin.serviceLoader.weightRule.graveyard

import club.xiaojiawei.enums.CardTypeEnum
import lin.domain.WarInfo
import lin.lifecycle.RuleGameLifecycle
import lin.serviceLoader.weightRule.AddWeightByCondition
import lin.warExt.common.getGraveyardCardsByType
import lin.weightHandler.condition.context.CostWeight
import lin.weightHandler.condition.context.UnUseWeight

class GraveyardByMinion : AddWeightByCondition, RuleGameLifecycle {
    private var minionNum = 0
    override fun calculateWeight(warInfo: WarInfo): Double {
        if (minionNum < 2) {
            minionNum = warInfo.getGraveyardCardsByType(CardTypeEnum.MINION).size
            if (minionNum > 2) minionNum = 2
        }
        if (minionNum == 0) return UnUseWeight
        return groupWeight * minionNum
    }

    override fun id() = 25080801
    override fun description(): String {
        return "亡者复生"
    }

    override var groupWeight = CostWeight
    override fun start() {
        minionNum = 0
    }


}