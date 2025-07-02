package lin.weightHandler.condition

import club.xiaojiawei.bean.Card
import lin.bean.ComboWeightInfo
import lin.dao.ComboCard
import lin.dao.WarInfo
import lin.weightHandler.condition.context.CostWeight
import lin.weightHandler.condition.context.defaultWeight

//判断的类型处理都在这里

/**
 * 手牌区域为条件
 *
 */
abstract class HandArea :  DefaultOutCardCondition(){
    override fun canUse(callCard: ComboCard, warInfo: WarInfo)= canUse(callCard,warInfo.getHandComboCards())
    abstract fun canUse(callCard: ComboCard, handCards: List<ComboCard>)
}

/**
 *
 */
abstract class GraveyardArea :  DefaultOutCardCondition(){
    override fun canUse(callCard: ComboCard, warInfo: WarInfo)= canUse(warInfo.getGraveyardCards())
    abstract fun canUse(graveyardCards: List<Card>)
}

//默认
abstract class DefaultOutCardCondition: OutCardCondition(), DepByWeightInfo

//可以一起打出
abstract class ComboCondition:OutCardCondition(), DepByWeightInfo {
    override fun canUse(callCard: ComboCard, warInfo: WarInfo) {
       return  canUse(callCard, warInfo.getHandComboCards())
    }

    abstract fun  canUse(callCard: ComboCard, handCards: List<ComboCard>)
    //一起打出有额外收益的卡牌

    //收益组默认之后打出
    open fun after() = true

}

//todo 暂时放这里
class ComboConditionByRace: ComboCondition() {

    override fun canUse(callCard: ComboCard, handCards: List<ComboCard>) {
        var count : Int = 0
        handCards.forEach{
                //判断依据没有写
                it.setComboWeightAndId{ handCards ->
                    if(handCards.first().groupId()==callCard.groupId()){
                        CostWeight
                    }else{
                        defaultWeight
                    }
                }
                count++

        }
        TODO()

    }

    override fun id(): Int {
        return 25062601
    }

    override fun initByWeightInfo(comboWeightInfoList: List<ComboWeightInfo>) {
        TODO("Not yet implemented")
    }
}