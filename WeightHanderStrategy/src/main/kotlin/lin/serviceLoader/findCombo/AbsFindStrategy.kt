package lin.serviceLoader.findCombo

import lin.domain.context.NotWeight
import lin.domain.result.*
import lin.domain.strategy.FindComboStrategy
import lin.domain.strategy.FindPlanner
import lin.domain.strategy.FindRule

/**
 * 这里是没有重新计算权重
 * 重新计算参考[lin.domain.strategy.ExtCostStrategy]
 */
abstract class AbsFindStrategy(var extCost: Int = 0, var extWeight: Double = NotWeight, val findRule: FindRule) :
    FindComboStrategy {

    fun processResult(findPlanner: FindPlanner, weightResult: WeightResult): WeightResult {
        when (weightResult) {
            is EmptyWeightResult -> {
                emptyResultAction(findPlanner)
                return weightResult
            }

            is EndWeightResult -> {
                return findPlanner.copyResult(
                    weightResult, extCost, findRule

                ).compareSumWeight(weightResult, extWeight)
                {
                    resultAction(findPlanner, weightResult)
                }
            }
        }
    }

    abstract fun isExecute(findPlanner: FindPlanner): Boolean
    override fun find(findPlanner: FindPlanner): CmdPlanner {
        if (isExecute(findPlanner)) return ContinuePlanner
        val weightHandlerDomain = findPlanner.weightHandlerDomain
        val endWeightResult = weightHandlerDomain.findCombination()
        val result = processResult(findPlanner, endWeightResult)
        return result.toPlanner()
    }

    override fun find(findPlanner: FindPlanner, weightResult: WeightResult): WeightResult {
        if (isExecute(findPlanner)) return weightResult
        return processResult(findPlanner, weightResult)
    }

    abstract fun emptyResultAction(findPlanner: FindPlanner)
    open fun resultAction(findPlanner: FindPlanner, endWeightResult: EndWeightResult) {
        emptyResultAction(findPlanner)
    }
}