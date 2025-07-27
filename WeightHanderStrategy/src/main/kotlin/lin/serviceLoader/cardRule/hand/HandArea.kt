package lin.serviceLoader.cardRule.hand

import lin.bean.ComboCard
import lin.domain.MyWarInfo
import lin.serviceLoader.cardRule.SetWeightByCondition

import lin.serviceLoader.cardRule.WeightCondition
import lin.weightHandler.condition.context.CostWeight

interface HandArea : SetWeightByCondition {
    override fun calculateSetWeight(myWarInfo: MyWarInfo): Double {
        return  onWarInfoProcessWeight(myWarInfo.getHandComboCards())
    }
    fun onWarInfoProcessWeight( handCards: List<ComboCard>):Double
}
abstract class AbstractHandArea : HandArea{
    override var groupWeight: Double = CostWeight

}

interface CanUseHandByLeaveCost : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo){
        val leaveCost =  myWarInfo.getNowCost() - callCard.getCost()
        val cardByLeaveCost =   myWarInfo.getCanUseCardsByCost().filter { it.getCost()<leaveCost }
        onWarInfoProcessWeight(callCard,cardByLeaveCost)
    }

    fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>)
}




