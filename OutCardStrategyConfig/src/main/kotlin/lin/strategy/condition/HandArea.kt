package lin.strategy.condition

import club.xiaojiawei.bean.Card
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.util.CardDBUtil
import lin.bean.v2.ComboWeightInfo

/**
 * 手牌区域为条件
 *
 */
abstract class HandArea :  OutCardCondition(),DepByWeightCards{
    abstract fun  canUse(handCards: List<Card>) : Double
}

/**
 * 以种族作为打出条件
 */
class HandAreaByRace:HandArea() {


    private var cache: List<CardRaceEnum> = emptyList()

    override fun canUse(handCards: List<Card>): Double {
        if(cache.isEmpty()) {
            val msg = "条件组件没有初始化或者没有条件组信息"
            log.warn { msg }
            throw ConditionException(this,msg)
        }
        return if(handCards.any {
                cache.any { cardRaceEnum -> it.cardRace == cardRaceEnum }
            }
        ){
            notConditionWeight
        }else{
            defaultConditionWeight
        }
    }

    private fun parse(key: String): CardRaceEnum {
        CardDBUtil.queryCardById(key).let {
            if (it.isNotEmpty()) {
                return CardRaceEnum.fromString(it.first().type)
            } else {
                return CardRaceEnum.UNKNOWN
            }

        }
    }

    override fun id() = 13

    override fun setWeightCardsById(comboWeightInfoList: Map<String, ComboWeightInfo>) {
        if(comboWeightInfoList.isNotEmpty())
            cache = comboWeightInfoList.map {
                parse(it.key)
            }
    }
}
