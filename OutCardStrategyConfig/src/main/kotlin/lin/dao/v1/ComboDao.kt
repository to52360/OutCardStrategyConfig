package lin.dao.v1

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import club.xiaojiawei.bean.War

import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import lin.bean.v2.ComboGroup
import lin.strategy.ComboStrategy

import lin.bean.v2.ComboWeightInfo
import lin.weightHandler.condition.context.CostWeight


/**
 * todo 名字还没有想好
 * 参考数据
 * 执行攻击动作
 * [club.xiaojiawei.util.DeckStrategyUtil.Result.execAction]
 * mapstruct DaoDao复制 Mapper
 *
 * []
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */
class ComboDao(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>, private val war: War) {
    //存储转化权重信息
    private val warManage: WarManage

    //存储策略分组
    init {

        val infoMap: Map<String, ComboWeightInfo> = parse(weightConfigs)
        warManage = WarManage(war, infoMap)

    }
    //把配置信息转化成上下文信息
    private fun parse(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>): Map<String, ComboWeightInfo> {
        val strategyMap: Map<String, ComboStrategy> = emptyMap()
        val groupMap: Map<String, ComboGroup> = emptyMap()
        TODO()
    }

    var initResult: Boolean = false


    /**
     * 没有权重信息或者没有匹配对应策略
     */
    private fun hasValidStrategy(): Boolean {
        TODO()
    }

    /**
     * 返回出牌策略,给策略类
     */
    fun getOutCardLambda(): (War) -> Unit {

        TODO()
    }

    private fun executeOutCardStrategy() {

        //这里依赖局部变量,还是全局war属性呢?
        val comboCards = warManage.runConditionAndOrderWeight()
        when (comboCards.size) {
            0 -> return
            1 -> warManage.useCard(comboCards.first())
            else -> {
                if (comboCards.first().getCost() == warManage.getNowCost()) {//权重最后
                    warManage.useCard(comboCards.first())
                    return
                } else {
                    findAndUseCard(comboCards)

                }

            }

        }


    }

    // 3. 定义一个递归函数（回溯）来查找所有可能的组合 ai生成 待验证
    private fun findAndUseCard(comboCards: List<ComboCard>) {
        val cost = warManage.getNowCost() // 当前费用
        if (comboCards.first().getCost() == cost) {
            warManage.useCard(comboCards.first())
            return
        }


        // 2. 初始化用于寻找最佳组合的变量
        var bestCombination: List<ComboCard> = emptyList()
        // *** 核心改动 ***: 我们追踪的不再是最大权重，而是最大“有效分”
        // 初始化为一个非常小的值，确保任何合法地出牌都比它好
        var maxEffectiveScore = Double.NEGATIVE_INFINITY

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
                val card = comboCards[i]
                if (currentCost + card.getCost() <= cost) {
                    findBestCombination(
                        startIndex = i + 1,
                        currentCost = currentCost + card.getCost(),
                        currentWeight = currentWeight + card.varPowerWeight,
                        currentCombination = currentCombination + card
                    )
                }
            }
        }

        // 4. 启动回溯搜索
        // 初始状态是空组合，从索引0开始
        findBestCombination(0, 0, 0.0, emptyList())

        // 5. 执行找到的最佳出牌组合
        if (bestCombination.isNotEmpty()) {
            val finalCost = bestCombination.sumOf { it.getCost() }
            val finalWeight = bestCombination.sumOf { it.varPowerWeight }
            println("找到最优出牌组合 (总费用: $finalCost, 总权重: $finalWeight, 有效分: $maxEffectiveScore): ${bestCombination.map { it.card.cardId }}")

            bestCombination.forEach { warManage.useCard(it) }
        }
    }


}

private val defaultStrategy: HsRadicalDeckStrategy by lazy {
    HsRadicalDeckStrategy()
}

//默认策略
val defaultOutCardLambda: (War) -> Unit = {
    defaultStrategy.executeOutCard()
}

