package lin.domain.strategy

import lin.bean.COINGroupId
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.domain.WeightHandlerDomain
import lin.domain.context.HalfCostWeight
import lin.domain.context.NotWeight
import lin.domain.result.ContinueWeight
import lin.domain.result.EmptyWeightResult
import lin.domain.result.EndWeightResult
import lin.domain.result.WeightResult
import lin.domain.strategy.ExtCostStrategy.Companion.extCostConfig
import lin.domain.strategy.ExtCostStrategy.Companion.extCostPredicate
import lin.domain.strategy.FindComboStrategy.Companion.DEF_PRIORITY
import lin.domain.strategy.FindComboStrategy.Companion.EXT_COST_PRIORITY
import lin.myLog
import lin.serviceLoader.cardInfoProvide.COINProvide
import lin.warExt.my.base.getCost


typealias FindCondition = (ComboCard) -> Boolean
typealias ExtCostConfig = (Int, List<ComboCard>) -> Pair<Int, Double>
typealias FindNowResult = Pair<Map<Boolean, List<ComboCard>>, WeightResult>
/**
 * 查询组合策略
 */
interface FindComboStrategy {
    companion object {
        const val EXT_COST_PRIORITY = 5
        const val DEF_PRIORITY = 10
    }

    fun priority(): Int
    fun find(findPlanner: FindPlanner): WeightResult
}

// 扩展函数：只有当存在满足 predicate 的卡时，才执行 block
inline fun FindPlanner.findIfAny(
    predicate: (ComboCard) -> Boolean,
    block: FindPlanner.() -> WeightResult
): WeightResult {
    val canUseCards = warManage.canUseCards
    if (!canUseCards.any(predicate)) {
        return ContinueWeight
    }
    return block() // 在 this = FindPlanner 上下文中执行
}


class DefFindStrategy : FindComboStrategy {
    override fun priority(): Int {
        return DEF_PRIORITY
    }

    override fun find(
        findPlanner: FindPlanner
    ): WeightResult {
        val weightHandlerDomain = findPlanner.weightHandlerDomain
        return weightHandlerDomain.findCombination()
    }

}

fun ComboCard.extCost(): Int {
    return this.cardWeightInfo?.cardContext?.getMetadata(COINProvide.coinKey) ?: 0
}

class ExtCostStrategy : FindComboStrategy {

    companion object {
        val extCostPredicate: FindCondition = { it.useGroupId == COINGroupId }
        val extCostConfig: ExtCostConfig = { cost, extCostCards ->
            var reduceWeight = NotWeight
            val extCost = extCostCards.sumOf { it.extCost() }
            if (cost < 5) reduceWeight = extCost * HalfCostWeight
            Pair(extCost, reduceWeight)
        }
    }

    override fun priority(): Int {
        return EXT_COST_PRIORITY
    }


    override fun find(findPlanner: FindPlanner): WeightResult {
        return findPlanner.findIfAny(extCostPredicate) {
            evaluateCurrentCombos(extCostPredicate).evaluateWithSkippedCards({ skipCard ->
                extCostConfig(warManage.getCost(), skipCard)
            }) { skip ->
                skip.forEach {
                    warManage.tryUseCard(it)
                }
            }
        }

    }
}

class FindPlanner(val warManage: MyWarManage, val weightHandlerDomain: WeightHandlerDomain) {
    fun evaluateCurrentCombos(extCostPredicate: FindCondition): FindNowResult {
        val canUseCardsByCost = warManage.canUseCards
        val canUseCardsByGroup = canUseCardsByCost.groupBy { extCostPredicate(it) }
        val canUseCard = canUseCardsByGroup.get(false)
        var nowWeightResult: WeightResult = EmptyWeightResult
        canUseCard?.let {
            nowWeightResult = weightHandlerDomain.findCombination(canUseCardsByCost = it)
        }
        return Pair(canUseCardsByGroup, nowWeightResult)
    }

    /**
     * 两个lambda的集合都是过滤条件一致
     */
    inline fun FindNowResult.evaluateWithSkippedCards(
        calWeightAndExtCost: (List<ComboCard>) -> Pair<Int, Double>,
        consumer: (List<ComboCard>) -> Unit
    ): WeightResult {
        val canUseCardsByGroup = this.first
        val nowWeightResult = this.second
        val skipCard = canUseCardsByGroup.get(true)
        skipCard?.let { skipCard ->
            val (extCost, extWeight) = calWeightAndExtCost(skipCard)
            val extWeightResult = evaluateWithExtra(extCost, skipCard)
            if (extWeightResult is EndWeightResult) {
                extWeightResult.extWeight = extWeight
            }
            return compareSumWeight(nowWeightResult, extWeightResult) { consumer(skipCard) }
        }
        myLog.warn { "没有比较数据,可能分组函数有问题,返回原始组合" }
        return nowWeightResult
    }

    fun evaluateWithExtra(extCost: Int, skipCard: List<ComboCard>): WeightResult {
        warManage.consumeExtCost(extCost) { sumExtCost ->
            val comboCards = warManage.canUseCardsByCost(sumExtCost).copy(skipCard)
            val extCostWeightResult = weightHandlerDomain.findCombination(sumExtCost, comboCards)
            return extCostWeightResult
        }
        // 不可能执行到这里
        throw IllegalStateException("Unreachable code")
    }

    fun evaluateExtCost(
        compareResult: WeightResult,
        findCondition: FindCondition = extCostPredicate,
        config: ExtCostConfig = extCostConfig
    ): WeightResult {
        return findIfAny(findCondition) {
            val warManage = warManage
            warManage.reLoad()
            val extCostCard = warManage.canUseCards.filter(findCondition)
            val (extCost, reduceWeight) = config(warManage.getCost(), extCostCard)
            val extWeightResult = evaluateWithExtra(extCost, extCostCard)
            if (extWeightResult is EndWeightResult) {
                extWeightResult.extWeight = reduceWeight
            }
            compareSumWeight(compareResult, extWeightResult) {
                extCostCard.forEach {
                    warManage.tryUseCard(it)
                }

            }
        }

    }

    fun List<ComboCard>.copy(skipComboCards: List<ComboCard>): List<ComboCard> {
        return this.filter { it !in skipComboCards }.map { warManage.parseComboCard(it.card) }
    }

    inline fun compareSumWeight(
        weightResult: WeightResult,
        extWeightResult: WeightResult,
        runnable: () -> Unit
    ): WeightResult {
        val nowWeight = weightResult.weightSum()
        weightResult.log()
        val extCostWeight = extWeightResult.weightSum()
        extWeightResult.log()
        if (nowWeight >= extCostWeight) {
            return weightResult
        } else {
            runnable()
            return extWeightResult
        }
    }
}








