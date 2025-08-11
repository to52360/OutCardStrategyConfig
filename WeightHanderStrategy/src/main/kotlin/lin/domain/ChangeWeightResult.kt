package lin.domain

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.myLog
import lin.weightHandler.condition.context.NotWeight

class ChangeWeightResult(val cards: HashSet<Card>, comboCards: List<ComboCard>) {
    val keepCost = 2
    val changeWeight = mutableListOf<ComboCard>()
    val hasChangeRule = sortedSetOf<ComboCard>(compareByDescending { it.changeWeight })

    //存在重复,使用set去重
    val removeCards = hashSetOf<ComboCard>()

    init {
        comboCards.forEach { comboCard ->
            if (comboCard.changeWeight < NotWeight) {
                removeCards.add(comboCard)
            } else {
                changeWeight + comboCard
                comboCard.changeComboRule?.let { rule ->
                    hasChangeRule + comboCard
                }
            }
        }
    }

    fun processChangeCard() {
        if (hasChangeRule.isEmpty()) {
            myLog.info { "没有换牌规则" }
            processNotChangeRule()
        } else {
            val fitRule = hashSetOf<ComboCard>()
            hasChangeRule.forEach { ruleComboCard ->
                var maxWeight = NotWeight
                var maxWeightComboCard: ComboCard? = null
                if (changeWeight.isNotEmpty()) {
                    val iterator = changeWeight.iterator()
                    while (iterator.hasNext()) {
                        val chaComCard = iterator.next()
                        if (ruleComboCard != chaComCard) {
                            var ruleWeight = NotWeight
                            ruleComboCard.changeComboRule!!.forEach {
                                ruleWeight = ruleWeight + it(chaComCard)
                            }
                            myLog.info { "ruleComboCard: $ruleComboCard 和 chaComCard: $chaComCard 的组合权重:$ruleWeight" }
                            if (ruleWeight < NotWeight) {//不能呆在一起
                                iterator.remove()
                                removeCards.add(chaComCard)
                            } else if (ruleWeight > NotWeight) {//目前组合为2个一组,多出来移除
                                val chaWeight = chaComCard.changeWeight
                                if (chaWeight > maxWeight) {//这里移除不了
                                    maxWeight = chaWeight
                                    maxWeightComboCard?.run {
                                        removeCards.add(this)
                                    }
                                    maxWeightComboCard = chaComCard
                                } else {
                                    iterator.remove()
                                    removeCards.add(chaComCard)
                                }
                            }
                        }
                    }

                }
                maxWeightComboCard?.also { comboCard -> fitRule.add(comboCard) } ?: run {
                    if (ruleComboCard.getCost() > keepCost) removeCards.add(ruleComboCard)
                }

            }
            if (fitRule.isNotEmpty()) {
                val notProcessCard = changeWeight - fitRule
                notProcessCard.forEach {
                    if (it.getCost() > keepCost) {
                        removeCards.add(it)
                    }
                }
            }
            remove()
        }

    }

    private fun processNotChangeRule() {
        var removeAll = true
        changeWeight.removeIf {
            val cost = it.getCost()
            if (removeAll) {
                //todo-future 这里费用判断要不要写死
                if (cost == 1 || (cost <= keepCost && it.changeWeight > NotWeight))
                    removeAll = false
            }
            val isMore2 = cost > keepCost
            if (isMore2) {
                removeCards.add(it)
            }
            isMore2


        }
        if (removeAll) {
            myLog.info { "移除全部" }
            cards.clear()
        } else {
            remove()
        }
    }

    private fun remove() {
        myLog.info { "移除的卡牌:$removeCards" }
        removeCards.forEach {
            cards.remove(it.card)
        }
    }
}