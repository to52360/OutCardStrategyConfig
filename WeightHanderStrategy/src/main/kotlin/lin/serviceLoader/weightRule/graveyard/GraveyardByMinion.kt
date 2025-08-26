package lin.serviceLoader.weightRule.graveyard


import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.domain.context.UnUseWeight
import lin.lifecycle.GameLifecycle
import lin.myLog
import lin.serviceLoader.weightRule.AddWeightByWarInfo
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicates
import lin.serviceLoader.weightRule.utils.PredicateByGroupIds
import lin.warExt.action.cleanPlay
import lin.warExt.base.getHandCards
import lin.warExt.base.getNowCost
import lin.warExt.common.getGraveyardCardsByType
import lin.warExt.rival.rivalAllCardsByPlayArea

class GraveyardByMinion : AddWeightByWarInfo, GameLifecycle,
    DepByWeightGroupIdDelegate<DepToPredicates> by PredicateByGroupIds() {
    private var minionNum = 0
    override fun calculateWeight(warInfo: WarInfo): Double {
        if (minionNum != 2) {

            minionNum = warInfo.getGraveyardCardsByType(CardTypeEnum.MINION).size
            //处理没资源,不符合条件也要打
            if (minionNum > 2) {
                if (warInfo.getHandCards().size < 4 || !depToPredicate(warInfo.handComboCards)) {
                    clean(warInfo)
                    return groupWeight
                }

            }
            myLog.info { "墓场随从数量:$minionNum" }
            //todo-future 墓场的随从统计数据有问题,暂时这样写
            if (warInfo.getNowCost() < 6) {
                minionNum = minionNum / 2
                if (minionNum < 2) {
                    clean(warInfo)
                    minionNum = warInfo.getGraveyardCardsByType(CardTypeEnum.MINION).size / 2
                }
            }



            if (minionNum > 2) minionNum = 2
        }
        return if (minionNum == 2)
            groupWeight
        else
            UnUseWeight

    }

    fun clean(warInfo: WarInfo) {
        if (warInfo.rivalAllCardsByPlayArea().isNotEmpty()) {
            myLog.info { "清理战场之后再使用" }
            warInfo.cleanPlay()
        }
    }


    override fun description(): String {
        return "亡者复生"
    }

    override var groupWeight = CostWeight
    override fun start() {
        minionNum = 0
    }


}