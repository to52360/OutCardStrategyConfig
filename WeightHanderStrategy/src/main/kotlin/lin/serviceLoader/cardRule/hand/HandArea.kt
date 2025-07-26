package lin.serviceLoader.cardRule.hand

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.serviceLoader.cardRule.DepByWeightInfo
import lin.serviceLoader.cardRule.HandArea
import lin.serviceLoader.cardRule.utils.infoToHandRacePredicate

import lin.weightHandler.condition.context.DefaultWeight
import lin.weightHandler.condition.context.NotConditionDefaultWeight


/**
 * 以种族作为打出条件
 */
class HandAreaByRace : HandArea, DepByWeightInfo {


    private lateinit  var handRacePredicate: (List<ComboCard>) -> Boolean

    override fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>) {
        var weight = DefaultWeight
        if (handRacePredicate(handCards)) {
            weight = NotConditionDefaultWeight
        }
        callCard.varPowerWeight = weight
    }


    override fun id(): Int = 250625013



    override fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        handRacePredicate = cardWeightInfoList.infoToHandRacePredicate()
    }
}

