package lin.serviceLoader.weightRule.afterRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.WeightCondition
import lin.warExt.common.hasTaunt
import lin.warExt.rival.rivalIsNotCardByPlayArea

/**
 * 光环类,后置规则,暂没有优先级,采用isBaseWeight,来判断有没有前置规则满足
 * [ComboCard.isBaseWeight]
 * todo-future 此类可以作为全局权重处理,但需要卡牌类型数据支持
 */

class HaloRule : WeightCondition {

    /**
     * 没有判断手牌是否存在后续收益
     */
    override fun calculateWeight(callCard:ComboCard,warInfo: WarInfo): Double {
        var addWeight = 0.0
        if(callCard.isBaseWeight()){
            if(warInfo.rivalIsNotCardByPlayArea())  addWeight += groupWeight
            if (warInfo.hasTaunt()) addWeight += groupWeight/2
        }
        return addWeight
    }

    override fun description(): String {
        return "光环类,后置规则,暂没有优先级,采用isBaseWeight,来判断有没有前置规则满足"
    }

    override var groupWeight = CostWeight

}