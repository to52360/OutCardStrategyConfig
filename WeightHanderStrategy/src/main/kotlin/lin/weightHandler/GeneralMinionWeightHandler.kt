package lin.weightHandler

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.ComboCard
import lin.bean.comboCardUtils.base.isMinion
import lin.domain.MyWarManage

import lin.domain.context.CostWeight

import lin.domain.context.MaxCostWeight
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.myLog

/**
 * 通用随从权重计算,怎么分开还没有考虑好
 */
class GeneralMinionWeightHandler : WeightHandler, DiscoverWeightHandler {

    private val cache  = hashMapOf<String,Double>()
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage): Double {
        val result = processPlayFull(callCard, warManage)
        return if (result == UnUseWeight) {
            result
        } else {
            cardWeight(callCard)
        }
    }
    override fun cardWeight(comboCard: ComboCard):Double{
        val card = comboCard.card
        if (!(comboCard.isBaseWeight() && card.isMinion())) return NotWeight


        val traitWeight = cache.getOrPut(card.cardId) {
            val weigh = getWeigh(card) * CostWeight
            myLog.info {
                "${card.entityName}的特征权重:${weigh}"
            }
            weigh
        }
        var baseWeight = (card.atc + card.health).toDouble() / 2 - card.cost + traitWeight
        myLog.info {
            "${card.entityName}通用随从计算:${baseWeight}"
        }
        //避免权重太夸张
        if (baseWeight > MaxCostWeight) baseWeight = MaxCostWeight

        return baseWeight
    }

    /**
     * 处理战场已满
     */
    fun processPlayFull(callCard: ComboCard, warManage: MyWarManage): Double {
        if (callCard.isMinion()) {
            return warManage.processPlayCardIsFull()
        }
        return NotWeight
    }


}
/**
 * 参考
 * [club.xiaojiawei.util.DeckStrategyUtil.clean]
 * 权重计算
 */
fun getWeigh(card: Card):Double {
    var value = 0.0

    if (card.isDeathRattle) {
        value += 0.3
    }
    if (card.isTaunt) {
        value += 0.1
    }
    if (card.isAdjacentBuff) {
        value += 0.3
    }
    if (card.isAura) {
        value += 0.3
    }
    if (card.isWindFury) {
        value += 0.15
    }
    if (card.isMegaWindfury) {
        value += 0.4
    }

    if (card.isTriggerVisual) {
        value += 0.1
    }
    if (card.isPoisonous) {
        value += 0.1
    }
    value += card.spellPower * 0.1
    return value

}