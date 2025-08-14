package lin.serviceLoader.weightRule.graveyard


import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.domain.context.UnUseWeight
import lin.lifecycle.GameLifecycle
import lin.myLog
import lin.serviceLoader.weightRule.AddWeightByWarInfo
import lin.warExt.action.cleanPlay
import lin.warExt.common.getGraveyardCardsByType

class GraveyardByMinion : AddWeightByWarInfo, GameLifecycle {
    private var minionNum = 0
    override fun calculateWeight(warInfo: WarInfo): Double {
        if (minionNum != 2) {
            warInfo.cleanPlay()
            minionNum = warInfo.getGraveyardCardsByType(CardTypeEnum.MINION).size / 2
            myLog.info { "墓场随从数量:$minionNum" }
            if (minionNum > 2) minionNum = 2
        }
        return if (minionNum == 2)
            groupWeight
        else
            UnUseWeight

    }

    override fun description(): String {
        return "亡者复生"
    }

    override var groupWeight = CostWeight
    override fun start() {
        minionNum = 0
    }


}