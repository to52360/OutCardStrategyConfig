package lin.domain.strategy

import lin.bean.COINGroupId
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.domain.WeightHandlerDomain
import lin.domain.context.HalfCostWeight
import lin.domain.context.NotWeight
import lin.domain.result.ContinueWeight
import lin.domain.result.EmptyWeightResult
import lin.domain.result.WeightResult
import lin.domain.strategy.FindComboStrategy.Companion.DEF_PRIORITY
import lin.domain.strategy.FindComboStrategy.Companion.EXT_COST_PRIORITY
import lin.serviceLoader.cardInfoProvide.COINProvide

/**
 * 查询组合策略
 */
interface FindComboStrategy {
    companion object {
        const val EXT_COST_PRIORITY = 5
        const val DEF_PRIORITY = 10
    }

    fun priority(): Int
    fun find(warManage: MyWarManage, weightHandlerDomain: WeightHandlerDomain): WeightResult
}

class DefFindStrategy : FindComboStrategy {
    override fun priority(): Int {
        return DEF_PRIORITY
    }

    override fun find(
        warManage: MyWarManage,
        weightHandlerDomain: WeightHandlerDomain
    ): WeightResult {
        return weightHandlerDomain.findCombination()
    }

}

class ExtCostFindStrategy : FindComboStrategy {
    companion object {
        val extCostPredicate: (ComboCard) -> Boolean = { it.useGroupId == COINGroupId }
    }

    override fun priority(): Int {
        return EXT_COST_PRIORITY
    }

    fun ComboCard.extCost(): Int {
        return this.cardWeightInfo?.cardContext?.getMetadata(COINProvide.coinKey) ?: 0
    }

    override fun find(warManage: MyWarManage, weightHandlerDomain: WeightHandlerDomain): WeightResult {
        val canUseCardsByCost = warManage.canUseCards
        val isFind = canUseCardsByCost.any(extCostPredicate)
        if (!isFind) {
            return ContinueWeight
        }
        val canUseCardsByGroup = canUseCardsByCost.groupBy { extCostPredicate(it) }
        val canUseCard = canUseCardsByGroup.get(false)
        var nowWeightResult: WeightResult = EmptyWeightResult
        canUseCard?.let {
            nowWeightResult = weightHandlerDomain.findCombination(canUseCardsByCost = it)
        }
        fun List<ComboCard>.copy(skipComboCards: List<ComboCard>): List<ComboCard> {
            val comboCards = mutableListOf<ComboCard>()
            forEach {
                if (!skipComboCards.any { skipComboCard -> skipComboCard == it }) {//重写的equals,不知道==起效不
                    val comboCard = warManage.parseComboCard(it.card)
                    comboCards.add(comboCard)
                }

            }
            return comboCards
        }

        val nowWeight = nowWeightResult.weightSum()
        nowWeightResult.log()

        val extCostCard = canUseCardsByGroup.get(true)!!
        val extCost = extCostCard.sumOf { it.extCost() }
        warManage.consumeExtCost(extCost) { sumExtCost ->
            val comboCards = warManage.canUseCardsByCost(sumExtCost).copy(extCostCard)
            val extCostWeightResult = weightHandlerDomain.findCombination(sumExtCost, comboCards)

            //处理没有配置权重,一直不使用硬币的情况
            var reduceWeight = NotWeight
            if (sumExtCost < 5) reduceWeight = extCost * HalfCostWeight


            val extCostWeight = extCostWeightResult.weightSum() - reduceWeight
            extCostWeightResult.log()
            if (nowWeight >= extCostWeight) {
                return nowWeightResult
            } else {
                extCostCard.forEach {
                    warManage.tryUseCard(it)
                }

                return extCostWeightResult
            }
        }
        return EmptyWeightResult
    }
}








