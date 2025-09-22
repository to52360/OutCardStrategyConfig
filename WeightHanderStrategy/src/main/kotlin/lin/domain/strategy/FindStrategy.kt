package lin.domain.strategy

import lin.bean.ComboCard
import lin.domain.WeightHandlerDomain
import lin.domain.context.HalfCostWeight
import lin.domain.context.NotWeight
import lin.domain.result.EmptyWeightResult
import lin.domain.result.EndWeightResult
import lin.domain.result.WeightResult

/**
 * 查询组合策略
 */
interface FindStrategy {
    fun find(weightResult: EndWeightResult, weightHandlerDomain: WeightHandlerDomain): WeightResult
}


/**
 * 额外费用查找组合策略
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

        warManage.consumeExtCost(cost) { extCost ->
            val comboCards = warManage.canUseCardsByCost(extCost).copy(extCostCard)
            val extCostWeightResult = weightHandlerDomain.findCombination(extCost, comboCards)

            //处理没有配置权重,一直不使用硬币的情况
            var reduceWeight = NotWeight
            if (nowWeight > HalfCostWeight) reduceWeight = cost * HalfCostWeight


            val extCostWeight = extCostWeightResult.weightSum() - reduceWeight
            extCostWeightResult.log()
            if (nowWeight >= extCostWeight) {
                return nowWeightResult
            } else {
                warManage.useCardAndRemove(extCostCard)
                return extCostWeightResult
            }
        }
        return EmptyWeightResult
    }

}

/**
 * 变更手牌策略
 * 主要是优化性能,加不加都没区别
 * 会导致栈溢出,用不上
 */
/*object ChangeStrategy : FindStrategy {
    override fun find(
        weightResult: EndWeightResult,
        weightHandlerDomain: WeightHandlerDomain
    ): WeightResult {
        val warManage = weightHandlerDomain.warManage
        warManage.useCardAndRemove(weightResult.pollFirstByHandler())
        warManage.refreshComboCards()
        return weightHandlerDomain.findCombination(canUseCardsByCost = warManage.canUseCards)
    }

}*/



