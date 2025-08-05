package lin.serviceLoader.cardRule.hand

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.warExt.base.getNowCost
import lin.serviceLoader.cardRule.AddWeightByCondition

import lin.serviceLoader.cardRule.WeightCondition
import lin.weightHandler.condition.context.CostWeight

interface HandArea : AddWeightByCondition {
    override fun calculateWeight(warInfo: WarInfo): Double {
        return  onWarInfoProcessWeight(warInfo.handComboCards)
    }
    fun onWarInfoProcessWeight( handCards: List<ComboCard>):Double
}
abstract class AbstractHandArea : HandArea{
    override var groupWeight: Double = CostWeight

}

interface CanUseHandByLeaveCost : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo){
        val leaveCost =  warInfo.getNowCost() - callCard.getCost()
        val cardByLeaveCost =   warInfo.canUseCards.filter { it.getCost()<leaveCost }
        onWarInfoProcessWeight(callCard,cardByLeaveCost)
    }

    fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>)
}




