package lin.serviceLoader.cardRule.hand

import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.serviceLoader.cardRule.AddWeightByCondition

import lin.serviceLoader.cardRule.WeightCondition
import lin.weightHandler.condition.context.CostWeight

interface HandArea : AddWeightByCondition {
    override fun calculateSetWeight(myWarManage: MyWarManage): Double {
        return  onWarInfoProcessWeight(myWarManage.readHandComboCards)
    }
    fun onWarInfoProcessWeight( handCards: List<ComboCard>):Double
}
abstract class AbstractHandArea : HandArea{
    override var groupWeight: Double = CostWeight

}

interface CanUseHandByLeaveCost : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, myWarManage: MyWarManage){
        val leaveCost =  myWarManage.getNowCost() - callCard.getCost()
        val cardByLeaveCost =   myWarManage.readCanUseCards.filter { it.getCost()<leaveCost }
        onWarInfoProcessWeight(callCard,cardByLeaveCost)
    }

    fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>)
}




