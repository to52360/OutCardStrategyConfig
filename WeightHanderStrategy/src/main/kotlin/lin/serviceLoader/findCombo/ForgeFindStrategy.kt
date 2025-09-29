package lin.serviceLoader.findCombo

import lin.domain.result.EndWeightResult
import lin.domain.result.WeightResult
import lin.domain.strategy.ExtCostStrategy.Companion.extCostPredicate
import lin.domain.strategy.FindComboStrategy
import lin.domain.strategy.FindComboStrategy.Companion.EXT_COST_PRIORITY
import lin.domain.strategy.FindCondition
import lin.domain.strategy.FindPlanner
import lin.domain.strategy.findIfAny

class ForgeFindStrategy : FindComboStrategy {
    companion object {
        const val FORGE_COST = -2
        const val FORGE_WEIGHT = 5.0
    }

    val forgeCondition: FindCondition = { comboCard ->
        comboCard.card.isForge
    }

    override fun priority() = EXT_COST_PRIORITY - 1 //比额外费用先

    override fun find(findPlanner: FindPlanner): WeightResult {
        val forgeResult = findPlanner.findIfAny(forgeCondition) {
            evaluateCurrentCombos { forgeCondition(it) || extCostPredicate(it) }
                .evaluateWithSkippedCards({ comboCards -> Pair(FORGE_COST, FORGE_WEIGHT) })
                //只锻造一个
                { comboCards -> comboCards.first().card.action.forge() }

        }
        val extCostResult = findPlanner.evaluateExtCost(forgeResult)
        if (extCostResult is EndWeightResult) return extCostResult
        return forgeResult
    }

}