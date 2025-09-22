package lin.weightHandler

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.domain.context.BaseWeight
import lin.domain.context.CostWeight
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
        if (result == UnUseWeight) {
            return result
        } else {
            return cardWeight(callCard)
        }
    }
    override fun cardWeight(comboCard: ComboCard):Double{
        val card = comboCard.card

        if(comboCard.powerWeight == BaseWeight ) {
            if (CardTypeEnum.MINION == card.cardType) {
                val c = cache[card.cardId + card.cost]
                if (c == null) {
                    val baseWeight = (card.atc + card.health) - (card.cost * 2)
                    myLog.info {
                        "${card.entityName}的基础权重:${baseWeight}"
                    }
                    val weigh = getWeigh(card)
                    myLog.info {
                        "${card.entityName}的特征权重:${weigh}"
                    }
                    var result = baseWeight + weigh * CostWeight

                    //费用增加的情况,严重亏模的情况 避免导致负数
                    if (result < 0) result = BaseWeight

                    //避免权重太夸张
                    if (result > CostWeight) result = CostWeight
                    cache[card.cardId + card.cost] = result
                    return result
                }
                return c

            }
        }
        return NotWeight
    }

    /**
     * 处理战场已满
     */
    fun processPlayFull(callCard: ComboCard, warManage: MyWarManage): Double {
        if (CardTypeEnum.MINION == callCard.card.cardType) {
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