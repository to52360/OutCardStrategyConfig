package lin.weightHandler.condition.implCondition

import lin.weightHandler.condition.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.weightHandler.condition.define.HandArea
import lin.weightHandler.condition.context.DefaultWeight
import lin.weightHandler.condition.context.NotConditionDefaultWeight


/**
 * 以种族作为打出条件
 */
class HandAreaByRace : HandArea  {


    private lateinit  var handRacePredicate: (List<ComboCard>) -> Boolean

    override fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>) {
        var weight = DefaultWeight
        if (handRacePredicate(handCards)) {
            weight = NotConditionDefaultWeight
        }
        callCard.varPowerWeight = weight
    }


    override fun id() = 250625013



    override fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        handRacePredicate =  cardWeightInfoList.infoToHandRacePredicate()
    }
}

