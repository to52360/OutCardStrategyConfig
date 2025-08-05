package lin.serviceLoader.cardRule.after

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.cardRule.AddWeightByAll
import lin.serviceLoader.cardRule.AddWeightByCondition
import lin.serviceLoader.cardRule.WeightCondition
import lin.warExt.common.hasTaunt
import lin.warExt.rival.rivalIsNotCardByPlayArea
import lin.weightHandler.condition.context.CostWeight

/**
 * 光环类,后置规则,暂没有优先级,采用isBaseWeight,来判断有没有前置规则满足
 * [ComboCard.isBaseWeight]
 * todo-future 此类可以作为全局权重处理,需要卡牌类型数据支持
 */

class HaloRule: AddWeightByAll {
    override fun id()= 25072901

    /**
     * 没有判断手牌是否存在后续收益
     */
    override fun calculateWeight(callCard:ComboCard,warInfo: WarInfo): Double {
        var addWeight = 0.0
        if(callCard.isBaseWeight()){
            if(warInfo.rivalIsNotCardByPlayArea())  addWeight += groupWeight
            if (warInfo.hasTaunt()) addWeight += groupWeight/2
        }else{
            addWeight -= groupWeight
        }
        return addWeight
    }

    override var groupWeight = CostWeight

}