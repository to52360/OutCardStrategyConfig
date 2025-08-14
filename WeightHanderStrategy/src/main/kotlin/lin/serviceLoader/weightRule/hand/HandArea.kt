package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.serviceLoader.weightRule.AddWeightByWarInfo
import lin.serviceLoader.weightRule.WeightCondition
import lin.warExt.base.getNowCost

interface HandArea : AddWeightByWarInfo {
    override fun calculateWeight(warInfo: WarInfo): Double {
        return  onWarInfoProcessWeight(warInfo.handComboCards)
    }
    fun onWarInfoProcessWeight( handCards: List<ComboCard>):Double
}
abstract class AbstractHandArea : HandArea{
    override var groupWeight: Double = CostWeight

}

/**
 * 除去本身剩余费用
 */
interface CanUseHandByLeaveCost : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo){
        val leaveCost = warInfo.getNowCost() - callCard.cost()
        val cardByLeaveCost = warInfo.canUseCards.filter { it.cost() < leaveCost }
        onLeaveCostProcessWeight(callCard, cardByLeaveCost)
    }

    override fun description(): String {
        return "除去本身剩余费用可以的手牌"
    }

    fun onLeaveCostProcessWeight(callCard: ComboCard, leaveCostHandCards: List<ComboCard>)
}




