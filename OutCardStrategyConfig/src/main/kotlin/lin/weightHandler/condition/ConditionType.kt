package lin.weightHandler.condition

import club.xiaojiawei.bean.Card
import lin.bean.v2.ComboWeightInfo
import lin.dao.v1.ComboCard
import lin.dao.v1.bean.WarInfo
import lin.weightHandler.condition.context.notValueDefaultWeight

//判断的类型处理都在这里

/**
 * 手牌区域为条件
 *
 */
abstract class HandArea :  DefaultOutCardCondition(){
    override fun canUse(callCard: ComboCard, warInfo: WarInfo)= canUse(callCard,warInfo.handCards)
    abstract fun canUse(callCard: ComboCard,handCards: List<ComboCard>)
}

/**
 *
 */
abstract class GraveyardArea :  DefaultOutCardCondition(){
    override fun canUse(callCard: ComboCard, warInfo: WarInfo)= canUse(warInfo.war.me.graveyardArea.cards)
    abstract fun canUse(graveyardCards: List<Card>)
}

//默认
abstract class DefaultOutCardCondition: OutCardCondition(), DepByWeightCards

//可以一起打出
abstract class ComboCondition:OutCardCondition(), DepByWeightCards {
    override fun canUse(callCard: ComboCard, warInfo: WarInfo) {
       return  canUse(callCard, warInfo.handCards)
    }

    abstract fun  canUse(callCard:ComboCard, handCards: List<ComboCard>)
    //一起打出有额外收益的卡牌

    //收益组默认之后打出
    open fun after() = true

}

//todo 暂时放这里
class ComboConditionByRace: ComboCondition() {

    override fun canUse(callCard:ComboCard,handCards: List<ComboCard>) {
        var count : Int = 0
        handCards.forEach{
            if(TODO()) {
                it.setComboWeightAndId(callCard.cardId(),notValueDefaultWeight)
                count++
            }
        }
        TODO()

    }

    override fun id(): Int {
        return 25062601
    }

    override fun setWeightCardsById(comboWeightInfoList: Map<String, ComboWeightInfo>) {
        TODO("Not yet implemented")
    }
}