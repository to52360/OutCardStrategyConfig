package lin.domain.result

import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.myLog

sealed class CmdPlanner
object ContinueWeight : CmdPlanner()
class ResultPlanner(val weightResult: WeightResult) : CmdPlanner()

sealed class WeightResult {
    open fun weightSum(): Double {
        return NotWeight
    }

    open fun log() {
        myLog.info { "Empty" }
    }
    open fun toPlanner(): ResultPlanner {
        return ResultPlanner(this)
    }
}

object EmptyWeightResult : WeightResult() {
    val emptyWeightResult = ResultPlanner(this)
    override fun toPlanner(): ResultPlanner {
        return emptyWeightResult
    }

}

class EndWeightResult(
    val canUseCards: List<ComboCard>,
    val cost: Int,
    val findStrategy: FindBestCombination = DefaultFindBestCombination
) : WeightResult() {
    //todo-future 存在直接操作权重,导致查找不到元素 想改成ArrayList,太复杂了,后面再说
    private val _canUseCardsByHandler = mutableListOf<ComboCard>()

    val unUseCards: MutableList<ComboCard>
        get() = _unUseCards

    //todo-future 存在直接操作权重,导致查找不到元素
    private val _unUseCards = mutableListOf<ComboCard>()
    var bestCombination: List<ComboCard> = emptyList()
        private set
    var extWeight = 0.0

    /**
     * 处理权重之后的挫折
     */
    fun processWeightAfter(comboCard: ComboCard) {
        if (comboCard.canUse()) _canUseCardsByHandler.add(comboCard)
        else _unUseCards.add(comboCard)
    }
    fun addAll(comboCards: List<ComboCard>) {
        _canUseCardsByHandler.addAll(comboCards)
    }

    /**
     * 判断是否可以直接使用
     */
    fun isLessCost(): Boolean {
        val result = _canUseCardsByHandler.size == 1 || _canUseCardsByHandler.sumOf { it.cost() } < cost
        return result
    }

    override fun weightSum() = bestCombination.sumOf { it.powerWeight }
    fun costSum() = bestCombination.sumOf { it.cost() } + extWeight
    fun notAbleUseCards(): Boolean = _canUseCardsByHandler.isEmpty()

    override fun log() {
        myLog.info { "costSum: ${costSum()},weightSum: ${weightSum()},成员:${bestCombination}" }
    }

    fun lessAbleUseCards(): List<ComboCard> {
        val lessAbleUseCards = _canUseCardsByHandler - bestCombination
        return lessAbleUseCards
    }

    fun findBestCombination() {
        if (isLessCost()) this.bestCombination = _canUseCardsByHandler
        else {
            this.bestCombination = findStrategy.findBestCombination(_canUseCardsByHandler, cost)
        }

    }

}

inline fun WeightResult.compareSumWeight(
    compareWeight: WeightResult,
    extWeight: Double,
    action: () -> Unit
): WeightResult {
    if (this.weightSum() >= compareWeight.weightSum() + extWeight) {
        action()
        return this
    } else {
        return compareWeight
    }

}