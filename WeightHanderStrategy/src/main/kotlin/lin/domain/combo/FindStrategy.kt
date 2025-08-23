package lin.domain.combo

import lin.bean.ComboCard
import lin.domain.WeightHandlerDomain
import lin.domain.context.HalfCostWeight
import lin.warExt.base.getNowCost

/**
 * 查询组合策略
 */
interface FindStrategy {
    fun find(weightResult: EndWeightResult, weightHandlerDomain: WeightHandlerDomain): WeightResult
}


/**
 * 额外费用策略
 */
class ExtCostStrategy(val cost: Int) : FindStrategy {
    override fun find(
        weightResult: EndWeightResult,
        weightHandlerDomain: WeightHandlerDomain
    ): WeightResult {
        var nowWeightResult: WeightResult = EmptyWeightResult
        val warManage = weightHandlerDomain.warManage
        val extCostCard = weightResult.pollFirstByHandler()
        if (!weightResult.notAbleUseCards()) {
            weightHandlerDomain.findBestCombination(weightResult)
            nowWeightResult = weightResult
        }
        val nowWeight = nowWeightResult.weightSum()
        nowWeightResult.log()



        /**
         * 存在性能问题,暂时这样了
         */
        fun List<ComboCard>.copy(skipComboCard: ComboCard): List<ComboCard> {
            val comboCards = mutableListOf<ComboCard>()
            forEach {
                if (skipComboCard != it.card) {//重写的equals,不知道==起效不
                    val comboCard = warManage.parseComboCard(it.card)
                    comboCards.add(comboCard)
                }

            }
            return comboCards
        }

        val extCost = warManage.getNowCost() + cost
        val comboCards = warManage.canUseCardsByCost(extCost).copy(extCostCard)
        val extCostWeightResult = weightHandlerDomain.findCombination(extCost, comboCards)
        val reduceWeight = cost * HalfCostWeight
        val extCostWeight = extCostWeightResult.weightSum() - reduceWeight
        extCostWeightResult.log()
        if (nowWeight > extCostWeight) {
            return nowWeightResult
        } else {
            warManage.useCardAndRemove(extCostCard)
            return extCostWeightResult
        }


    }

}

/**
 * 变更手牌策略
 * 主要是优化性能,加不加都没区别
 */
object ChangeStrategy : FindStrategy {
    override fun find(
        weightResult: EndWeightResult,
        weightHandlerDomain: WeightHandlerDomain
    ): WeightResult {
        val warManage = weightHandlerDomain.warManage
        warManage.useCardAndRemove(weightResult.pollFirstByHandler())
        warManage.refreshComboCards()
        return weightHandlerDomain.findCombination(canUseCardsByCost = warManage.canUseCards)
    }

}



