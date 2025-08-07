package lin.serviceLoader.weightRule.combo

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.serviceLoader.weightRule.ComboCondition

//todo-future 暂时这样 暂时没有想清楚一起打出的例子要怎么处理
class ComboConditionByRace : ComboCondition {

     fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>) {
        var count : Int = 0
        handCards.forEach{
            //todo 判断依据没有写
/*            it.setComboWeightAndId{ handCards ->
                if(handCards.first().groupId()==callCard.groupId()){
                    CostWeight
                }else{
                    NotWeight
                }
            }*/
            count++

        }
        TODO()

    }



     fun id(): Int {
        return 25062601
    }

     fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        TODO("Not yet implemented")
    }


}