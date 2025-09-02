package lin.weightHandler

import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.domain.context.HalfCostWeight
import lin.warExt.base.getCost
import lin.warExt.base.getPlayCardSize

/**
 * 场面太多随从
 * todo-future 逻辑不够严谨 需要"之后处理器"(AfterWeightHandler)才行,之后处理器还没想好怎么实现
 * [AfterWeightHandler]
 */
class MinionNumWeightHandler : WeightHandler {
    val maxMinionNum = 4
    val maxCostGap = 2
    val gapCostWeight = HalfCostWeight
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        if (warManage.getPlayCardSize() > maxMinionNum && callCard.card.cardType == CardTypeEnum.MINION) {
            val costGap = warManage.getCost() - callCard.cost()
            if (costGap >= maxCostGap) {
                callCard.addWeight(-gapCostWeight)

            }

        }
    }
}