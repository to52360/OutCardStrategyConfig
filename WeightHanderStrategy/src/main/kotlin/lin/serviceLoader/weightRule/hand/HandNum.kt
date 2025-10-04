package lin.serviceLoader.weightRule.hand

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getHandCards

/**
 * 手牌数量决定权重
 */
class HandNumWeight : AbsWeightCondition() {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return if (warInfo.getHandCards().size > number) groupWeight else unConditionWeight
    }
}

abstract class AbsNumWeight(val filterList: (WarInfo) -> List<Card>) : AbsWeightCondition() {
    override fun description(): String {
        return "根据数量"
    }
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val size = filterList(warInfo).size
        return if (size > number) {
            groupWeight
        } else unConditionWeight
    }

}

class DefHandNum : AbsNumWeight({
    it.getHandCards()
})

class HandNumMinion : AbsNumWeight({
    it.getHandCards().filter { it.cardType == CardTypeEnum.MINION }
})
