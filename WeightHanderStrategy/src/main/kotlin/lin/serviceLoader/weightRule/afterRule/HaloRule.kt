package lin.serviceLoader.weightRule.afterRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.attack.hasTaunt
import lin.warExt.rival.rivalFindMaxAtcMinion
import lin.warExt.rival.rivalNotHasMinion

/**
 * 光环类,后置规则,暂没有优先级,采用isBaseWeight,来判断有没有前置规则满足
 * [ComboCard.isBaseWeight]
 * todo-future 此类可以作为全局权重处理,但需要卡牌类型数据支持
 */

class HaloRule : AbsWeightCondition() {

    /**
     * 没有判断手牌是否存在后续收益
     */
    override fun calculateWeight(callCard:ComboCard,warInfo: WarInfo): Double {

        if (warInfo.rivalNotHasMinion() || warInfo.hasTaunt()) return groupWeight
        val rivalAtc = warInfo.rivalFindMaxAtcMinion()?.atc ?: 0
        if (rivalAtc < callCard.card.health) return groupWeight
        return unConditionWeight
    }

    override fun description(): String {
        return "光环类,后置规则,暂没有优先级,采用isBaseWeight,来判断有没有前置规则满足"
    }

    override var groupWeight = CostWeight

}