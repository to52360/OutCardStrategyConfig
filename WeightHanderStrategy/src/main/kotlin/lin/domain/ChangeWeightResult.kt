package lin.domain

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.myLog
import lin.weightHandler.condition.context.NotWeight

/**
 * 卡牌权重变更结果处理类
 * 负责处理卡牌权重变化后的结果，包括卡牌的移除、替换和规则匹配等逻辑
 *
 * @property cards 需要处理的卡牌集合
 * @property comboCards 组合卡牌列表
 */
class ChangeWeightResult(val cards: HashSet<Card>, comboCards: List<ComboCard>) {
    // 保留卡牌的最大费用阈值
    val keepCost = 2

    // 需要变更权重的卡牌列表
    val changeWeight = mutableListOf<ComboCard>()
    val hasChangeRule = sortedSetOf(compareByDescending<ComboCard> {
        it.changeWeight
    }.thenBy { it.card.entityId })

    // 需要移除的卡牌集合（使用set去重）
    val removeCards = hashSetOf<ComboCard>()

    /**
     * 初始化方法
     * 处理输入的组合卡牌，将其分类为需要移除的卡牌和需要变更权重的卡牌
     */
    init {
        comboCards.forEach { comboCard ->
            if (comboCard.changeWeight < NotWeight) {
                removeCards.add(comboCard)
            } else {
                changeWeight.add(comboCard)
                // 如果卡牌有变更规则，则添加到hasChangeRule集合
                comboCard.changeComboRule?.let { rule ->
                    hasChangeRule.add(comboCard)
                }
            }
        }
    }

    /**
     * 处理需要变更的卡牌
     * 根据是否有变更规则采取不同的处理策略
     */
    fun processChangeCard() {
        if (hasChangeRule.isEmpty()) {
            myLog.info { "没有换牌规则" }
            processNotChangeRule()
            return
        }

        val fitRule = evaluateRulesAndFindFitCards()
        if (fitRule.isEmpty()) {
            processNotChangeRule()
        } else {
            processUnmatchedCards(fitRule)
            remove()
        }
    }

    /**
     * 评估规则并找到符合条件的卡牌
     *
     * @return 符合规则的卡牌集合
     */
    private fun evaluateRulesAndFindFitCards(): HashSet<ComboCard> {
        val fitRule = hashSetOf<ComboCard>()
        hasChangeRule.forEach { ruleComboCard ->
            val bestMatch = findBestMatchingCard(ruleComboCard)
            bestMatch?.also {
                fitRule.add(it)
                changeWeight.remove(ruleComboCard)//不参与后续匹配
            } ?: run {
                if (ruleComboCard.cost() > keepCost) removeCards.add(ruleComboCard)
            }
        }
        return fitRule
    }

    /**
     * 找到与规则卡牌最匹配的卡牌
     *
     * @param ruleComboCard 包含规则的卡牌
     * @return 最匹配的卡牌，如果没有匹配则返回null
     */
    private fun findBestMatchingCard(ruleComboCard: ComboCard): ComboCard? {
        var maxWeight = NotWeight
        var maxWeightComboCard: ComboCard? = null

        val iterator = changeWeight.iterator()
        while (iterator.hasNext()) {
            val currentCard = iterator.next()
            if (ruleComboCard == currentCard) continue

            val ruleWeight = calculateRuleWeight(ruleComboCard, currentCard)
            myLog.info { "ruleComboCard: $ruleComboCard 和 currentCard: $currentCard 的组合权重:$ruleWeight" }

            when {
                ruleWeight < NotWeight -> {
                    iterator.remove()
                    removeCards.add(currentCard)
                }

                ruleWeight > NotWeight -> {
                    if (currentCard.changeWeight > maxWeight) {
                        maxWeight = currentCard.changeWeight
                        maxWeightComboCard?.run { removeCards.add(this) }
                        maxWeightComboCard = currentCard
                    } else {
                        iterator.remove()
                        removeCards.add(currentCard)
                    }
                }

            }
        }
        return maxWeightComboCard
    }

    /**
     * 计算规则卡牌与当前卡牌的权重
     *
     * @param ruleComboCard 包含规则的卡牌
     * @param currentCard 当前卡牌
     * @return 计算得到的权重
     */
    private fun calculateRuleWeight(ruleComboCard: ComboCard, currentCard: ComboCard): Double {
        var ruleWeight = NotWeight
        // 应用所有变更规则计算权重
        ruleComboCard.changeComboRule?.forEach { ruleWeight += it(currentCard) }
        return ruleWeight
    }

    /**
     * 处理不匹配规则的卡牌
     *
     * @param fitRule 已匹配规则的卡牌集合
     */
    private fun
            processUnmatchedCards(fitRule: HashSet<ComboCard>) {
        changeWeight.forEach { card ->
            if (!fitRule.contains(card) && card.cost() > keepCost) {
                removeCards.add(card)
            }
        }
    }

    /**
     * 处理没有变更规则的情况
     * 根据费用阈值决定保留或移除卡牌
     */
    private fun processNotChangeRule() {
        var removeAll = true
        changeWeight.forEach {
            val cost = it.cost()
            if (removeAll) {
                //todo-future 这里费用判断要不要写死
                if (cost < keepCost || (cost <= keepCost && it.changeWeight > NotWeight))
                    removeAll = false
            }
            val isMore2 = cost > keepCost
            if (isMore2) {
                removeCards.add(it)
            }
        }
        if (removeAll) {
            myLog.info { "移除全部" }
            cards.clear()
        } else {
            remove()
        }
    }

    /**
     * 执行卡牌移除操作
     * 从cards集合中移除所有标记为移除的卡牌
     */
    private fun remove() {
        myLog.info { "移除的卡牌:$removeCards" }
        removeCards.forEach {
            cards.remove(it.card)
        }
    }
}