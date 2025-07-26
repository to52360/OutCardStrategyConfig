package lin.implCondition

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.weightHandler.condition.define.ComboCondition
import lin.weightHandler.condition.context.CostWeight
import lin.weightHandler.condition.context.DefaultWeight

import lin.weightHandler.condition.define.HandArea


//todo-future 暂时这样 暂时没有想清楚一起打出的例子要怎么处理
class ComboConditionByRace: HandArea,ComboCondition {

    override fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>) {
        var count : Int = 0
        handCards.forEach{
            //todo 判断依据没有写
            it.setComboWeightAndId{ handCards ->
                if(handCards.first().groupId()==callCard.groupId()){
                    CostWeight
                }else{
                    DefaultWeight
                }
            }
            count++

        }
        TODO()

    }



    override fun id(): Int {
        return 25062601
    }

    override fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        TODO("Not yet implemented")
    }


}
