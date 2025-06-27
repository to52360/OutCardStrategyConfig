package lin.weightHandler.condition

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.util.CardDBUtil
import lin.bean.v2.ComboWeightInfo
import lin.dao.v1.ComboCard
import lin.weightHandler.condition.context.notValueDefaultWeight


/**
 * 以种族作为打出条件
 */
class HandAreaByRace: HandArea() {


    private var cache: List<CardRaceEnum> = emptyList()
    //优化
    override fun canUse(callCard: ComboCard,handCards: List<ComboCard>) {
        if(cache.isEmpty()) {
            val msg = "条件组件没有初始化或者没有条件组信息"
            log.warn { msg }
            throw ConditionException(this,msg)
        }
        var weight = notValueDefaultWeight
         if(handCards.any {
                cache.any { cardRaceEnum -> it.card.cardRace == cardRaceEnum }
            }
        ){
             weight =  notConditionWeight
        }
        callCard.varPowerWeight = weight

    }
    //应该移动公共位置
    private fun parse(key: String): CardRaceEnum {
        CardDBUtil.queryCardById(key).let {
            if (it.isNotEmpty()) {
                return CardRaceEnum.fromString(it.first().type)
            } else {
                return CardRaceEnum.UNKNOWN
            }

        }
    }

    override fun id() = 250625013


    override fun setWeightCardsById(comboWeightInfoList: Map<String, ComboWeightInfo>) {
        if(comboWeightInfoList.isNotEmpty())
            cache = comboWeightInfoList.map {
                parse(it.key)
            }
    }
}
