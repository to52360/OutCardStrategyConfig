package lin.serviceLoader.findCombo

import lin.domain.result.*
import lin.domain.strategy.FindComboStrategy
import lin.domain.strategy.FindComboStrategy.Companion.EXT_COST_PRIORITY
import lin.domain.strategy.FindPlanner
import lin.domain.strategy.FindRule
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getHandCards


class ForgeFindStrategy : FindComboStrategy {
    companion object {
        const val FORGE_COST = -2
        const val FORGE_WEIGHT = 5.0
    }

    val forgeRule: FindRule = { comboCard ->
        comboCard.card.isForge
    }

    override fun priority() = EXT_COST_PRIORITY + 1 //比额外费用后

    override fun find(findPlanner: FindPlanner): CmdPlanner {

        if (isExecute(findPlanner)) {
            return ContinueWeight
        }
        val weightHandlerDomain = findPlanner.weightHandlerDomain
        val endWeightResult = weightHandlerDomain.findCombination()
        val result = processResult(findPlanner, endWeightResult)
        return result.toPlanner()

    }

    override fun find(findPlanner: FindPlanner, weightResult: WeightResult): WeightResult {
        if (isExecute(findPlanner)) {
            weightResult
        }
        val result = processResult(findPlanner, weightResult)
        return result
    }

    private fun isExecute(findPlanner: FindPlanner): Boolean {
        val warManage = findPlanner.warManage
        val canUseCards = warManage.canUseCards
        return !canUseCards.any(forgeRule) || warManage.getCost() < 2
    }

    private fun processResult(findPlanner: FindPlanner, weightResult: WeightResult): WeightResult {
        when (weightResult) {
            is EmptyWeightResult -> {
                forge(findPlanner)
                return weightResult
            }

            is EndWeightResult -> {
                return findPlanner.copyResult(
                    weightResult, FORGE_COST, forgeRule

                ).compareSumWeight(weightResult, FORGE_WEIGHT)
                {
                    forge(findPlanner)
                }
            }
        }
    }

    private fun forge(findPlanner: FindPlanner) {
        val handCards = findPlanner.warManage.getHandCards()
        val forgeCards = handCards.filter { it.isForge }.minBy { it.cost }
        forgeCards.action.forge()
    }

}