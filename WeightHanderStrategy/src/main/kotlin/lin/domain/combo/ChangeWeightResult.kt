package lin.domain.combo


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.myLog

/**
 * 卡牌权重变更结果处理类
 * 负责处理卡牌权重变化后的结果，包括卡牌的移除、替换和规则匹配等逻辑
 * 属性
 * [lin.serviceLoader.parse.ParseCombo]
 * @property cards 需要处理的卡牌集合
 * @property comboCards 额外卡牌列表
 */
class ChangeWeightResult(val cards: HashSet<Card>, comboCards: List<ComboCard>) {
    // 保留卡牌的最大费用阈值
    val keepCost = 2

    // 需要变更权重的卡牌列表
    val changeWeight = mutableListOf<ComboCard>()

    //todo-future 存在直接操作权重,导致查找不到元素
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
                changeWeight.add(comboCard)
                // 如果卡牌有变更规则，则添加到hasChangeRule集合
                comboCard.changeComboRule?.let { rule ->
                    hasChangeRule.add(comboCard)
            }
        }
    }

    /**
     * 处理需要变更的卡牌
     * 根据是否有变更规则采取不同的处理策略
     */
    fun processChangeCard() {
        if (hasChangeRule.isEmpty()) {
            myLog.info { "没有组合规则启用默认规则" }
            processNotChangeRule()
            return
        }

        val fitRule = evaluateRulesAndFindFitCards()
        if (fitRule.isEmpty()) {
            myLog.info { "没有符合组合规则启用默认规则" }
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
                fitRule.add(ruleComboCard)
                changeWeight.remove(it)
                changeWeight.remove(ruleComboCard)//不参与后续匹配
            } ?: run {
                //大于保持费用,也不是其他规则配合牌就移除
                if ((isRemove(ruleComboCard)) && !fitRule.contains(ruleComboCard)) {
                    myLog.info { "没配合牌移除:$ruleComboCard" }
                    removeCards.add(ruleComboCard)
                }
            }
        }
        return fitRule
    }

    private fun isRemove(comboCard: ComboCard): Boolean {
        return comboCard.changeWeight < NotWeight || comboCard.cost() > keepCost
    }

    /**
     * 找到与规则卡牌最匹配的卡牌
     *
     * @param ruleComboCard 包含规则的卡牌
     * @return 最匹配的卡牌，如果没有匹配则返回null
     */
    private fun findBestMatchingCard(ruleComboCard: ComboCard): ComboCard? {
        var maxWeight = UnUseWeight
        var maxWeightComboCard: ComboCard? = null

        val iterator = changeWeight.iterator()
        while (iterator.hasNext()) {
            val currentCard = iterator.next()
            if (ruleComboCard == currentCard) continue

            val ruleWeight = calculateRuleWeight(ruleComboCard, currentCard)


            when {
                ruleWeight < NotWeight -> {
                    if (currentCard.cost() >= keepCost) {//小于费不移除
                        iterator.remove()
                        myLog.info { "移除互斥卡:$currentCard" }
                        removeCards.add(currentCard)
                    }
                }

                ruleWeight > NotWeight -> {
                    myLog.info { "规则卡: ${ruleComboCard.cardId()} 和 配合卡: ${currentCard.cardId()} 的配合卡权重:${currentCard.changeWeight}" }
                    if (currentCard.changeWeight > maxWeight) {
                        myLog.info { "${currentCard}设置最佳配合牌" }
                        maxWeight = currentCard.changeWeight
                        maxWeightComboCard?.run { removeCards.add(this) } //这里不能用迭代器删除
                        maxWeightComboCard = currentCard
                    } else {
                        myLog.info { "${currentCard}不是最佳配合牌移除" }
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
            if (!fitRule.contains(card) && isRemove(card)) {
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
            //todo-future 这里费用判断要不要写死,2费需要权重大于NotWeight
            if (cost < keepCost || (cost <= keepCost && it.changeWeight > NotWeight))
                removeAll = false
            else {
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
        /*        if(cards.size==BeginHandCardNum&&removeCards.isEmpty()){
                    for (card in changeWeight){
                        //等于0等于可抛弃
                        if(card.changeWeight==NotWeight){
                            removeCards.remove(card)
                            //只移除一个
                            break
                        }
                    }

                }*/
        removeCards.forEach {
            cards.remove(it.card)
        }
    }
}