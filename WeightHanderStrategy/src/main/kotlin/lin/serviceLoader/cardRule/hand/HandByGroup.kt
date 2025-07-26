package lin.serviceLoader.cardRule.hand

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.serviceLoader.cardRule.DepByWeightInfos
import lin.serviceLoader.cardRule.HandArea
import lin.weightHandler.condition.context.ConditionException


class HandByGroup : HandArea, DepByWeightInfos {

    private lateinit var groupIds : Array<Double> 
    override fun onWarInfoProcessWeight(
        callCard: ComboCard,
        handCards: List<ComboCard>
    ) {
        TODO("Not yet implemented")
    }

    override fun id()=25072601

    override fun initByWeightInfo(cardWeightInfoList: List<List<CardWeightInfo>>) {
        groupIds =
            TODO()
    }
}

