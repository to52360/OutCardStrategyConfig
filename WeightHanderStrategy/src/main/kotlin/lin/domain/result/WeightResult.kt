package lin.domain.result

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
    val cost: Int,
    val bestCombo: BestCombination = DefaultBestCombination
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

    /**
     * 处理权重之后的挫折
     */
    fun processWeightAfter(comboCard: ComboCard) {
        if (comboCard.useAble()) _canUseCardsByHandler.add(comboCard)
        else _unUseCards.add(comboCard)
    }

    /**
     * 判断是否可以直接使用
     */
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
        this.bestCombination = bestCombo.findBestCombination(_canUseCardsByHandler.toList(), cost)
    }
}