package lin.weightHandler

import club.xiaojiawei.bean.Card

import club.xiaojiawei.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.myLog
import lin.weightHandler.condition.context.CostWeight
import lin.weightHandler.condition.context.BaseWeight

/**
 * 通用随从权重计算
 */
class GeneralMinionWeightHandler : WeightHandler,CardWeightHandler {

    private val cache  = hashMapOf<String,Double>()
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        callCard.addWeight(cardWeight(callCard))
    }
    override fun cardWeight(comboCard: ComboCard):Double{
        if(comboCard.powerWeight == BaseWeight ) {
            val card = comboCard.card
            if (CardTypeEnum.MINION == card.cardType) {
                val c = cache[card.cardId + card.cost]
                if (c == null) {
                    val baseWeight = (card.atc + card.health) - card.cost * 2
                    myLog.info {
                        "${card.entityName}的基础权重:${baseWeight}"
                    }
                    val weigh = getWeigh(card)
                    myLog.info {
                        "${card.entityName}的特征权重:${weigh}"
                    }
                    var result = (baseWeight + weigh) * CostWeight
                    if (result < 0) result = BaseWeight //费用增加的情况,严重亏模的情况 可能导致负数
                    cache[card.cardId + card.cost] = result
                    return result
                }

            }
        }
        return BaseWeight
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