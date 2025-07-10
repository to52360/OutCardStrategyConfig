package lin.weightHandler

import club.xiaojiawei.bean.Card
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.dao.MyWarManage
import lin.weightHandler.condition.context.CostWeight
import lin.weightHandler.condition.context.baseWeight


class GeneralMinionWeightHandler : WeightHandler,CardWeightHandler {

    private val cache  = hashMapOf<String,Double>()
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        val card = callCard.card
        if(callCard.varPowerWeight== baseWeight ){
            callCard.varPowerWeight = cardWeight(card)
        }

    }
    override fun cardWeight(card: Card):Double{
        if( CardTypeEnum.MINION==card.cardType){
            val c = cache[card.cardId]
            //todo 消耗为0应该不进入缓存,费用原生才进入缓存
            if(c==null){
                val baseWeigh = (card.atc+card.health) -card.cost*2
                log.info{
                    "${card.entityName}的基础权重:${baseWeigh}"
                }
                val weigh = getWeigh(card)
                log.info{
                    "${card.entityName}的特征权重:${weigh}"
                }
                val result = (baseWeigh+weigh)* CostWeight
                cache[card.cardId] = result
                return result
            }

        }
        return baseWeight
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