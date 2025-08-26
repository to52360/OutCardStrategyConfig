package lin.domain.combo

import lin.bean.ComboCard
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.myLog
import java.util.*

sealed class WeightResult {
    open fun weightSum(): Double {
        return NotWeight
    }

    open fun log() {
        myLog.info { "Empty" }
    }
}

object EmptyWeightResult : WeightResult()
class EndWeightResult(
    val canUseCards: List<ComboCard>,
    val cost: Int
) : WeightResult() {
    //todo-future 存在直接操作权重,导致查找不到元素
    private val _canUseCardsByHandler =
        sortedSetOf(compareByDescending<ComboCard> { it.powerWeight }.thenBy { it.card.entityId })
    val canUseCardsByHandler: Set<ComboCard>
        get() = _canUseCardsByHandler
    val unUseCards: TreeSet<ComboCard>
        get() = _unUseCards

    //todo-future 存在直接操作权重,导致查找不到元素
    private val _unUseCards = sortedSetOf(compareByDescending<ComboCard> { it.powerWeight }.thenBy { it.card.entityId })
    var bestCombination: List<ComboCard> = emptyList()
        private set

    fun processWeightAfterAdd(comboCard: ComboCard) {
        if (comboCard.useAble()) _canUseCardsByHandler.add(comboCard)
        else _unUseCards.add(comboCard)
    }

    fun isLessCost(): Boolean {
        val result = _canUseCardsByHandler.size == 1 || _canUseCardsByHandler.sumOf { it.cost() } < cost
        if (result) bestCombination = _canUseCardsByHandler.toList()
        return result
    }

    override fun weightSum() = bestCombination.sumOf { it.powerWeight }
    fun costSum() = bestCombination.sumOf { it.cost() }
    fun notAbleUseCards(): Boolean = _canUseCardsByHandler.isEmpty()
    fun pollFirstByHandler(): ComboCard =
        _canUseCardsByHandler.pollFirst() ?: run { throw NoSuchElementException("不应该为null") }

    override fun log() {
        myLog.info { "costSum: ${costSum()},weightSum: ${weightSum()},成员:${bestCombination}" }
    }

    fun lessAbleUseCards(): Set<ComboCard> {
        val lessAbleUseCards = _canUseCardsByHandler - bestCombination
        return lessAbleUseCards
    }

    fun findBestCombination() {
        // 2. 初始化用于寻找最佳组合的变量
        var bestCombination: List<ComboCard> = emptyList()
        // *** 核心改动 ***: 我们追踪的不再是最大权重，而是最大“有效分”
        // 初始化为一个非常小的值，确保任何合法地出牌都比它好
        var maxEffectiveScore = Double.NEGATIVE_INFINITY

        val comboCards = _canUseCardsByHandler.toList()

        // 3. 定义一个递归函数（回溯）来查找所有可能的组合
        fun findBestCombination(
            startIndex: Int,
            currentCost: Int,
            currentWeight: Double,
            currentCombination: List<ComboCard>
        ) {
            // *** 核心改动 ***
            // 在每次形成一个有效组合时（包括空组合），都计算其“有效分”
            val remainingCost = cost - currentCost

            val penalty = remainingCost * CostWeight
            val effectiveScore = currentWeight - penalty

            // 如果当前组合的有效分超过了已知的最高分，则更新最佳组合
            if (effectiveScore > maxEffectiveScore) {
                maxEffectiveScore = effectiveScore
                bestCombination = currentCombination
            }

            // 从 startIndex 开始遍历，继续添加新的牌来探索更深的组合
            for (i in startIndex until comboCards.size) {
                val newCard = comboCards[i]
                if (newCard.cost() <= remainingCost) {
                    //同组加权
                    var comboBonus = NotWeight
                    //todo-future  默认无环形结构,无法处理环形结构
                    newCard.combo?.also {
                        currentCombination.forEach {
                            comboBonus += newCard.comboAddWeight(it)
                        }
                    } ?: run {
                        currentCombination.forEach { existingCard ->
                            // combo加权
                            comboBonus += existingCard.comboAddWeight(newCard)
                        }
                    }

                    findBestCombination(
                        startIndex = i + 1,
                        currentCost = currentCost + newCard.cost(),
                        currentWeight = currentWeight + newCard.powerWeight + comboBonus,
                        currentCombination = currentCombination + newCard
                    )
                }
            }
        }

        // 4. 启动回溯搜索
        // 初始状态是空组合，从索引0开始
        findBestCombination(0, 0, 0.0, emptyList())
        this.bestCombination = bestCombination
    }
}