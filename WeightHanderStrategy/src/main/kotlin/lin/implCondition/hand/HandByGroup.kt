package lin.implCondition.hand

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.weightHandler.condition.context.ConditionException
import lin.weightHandler.condition.define.HandArea

class HandByGroup : HandArea  {

    private  var groupId : Double = 0.0
    override fun onWarInfoProcessWeight(
        callCard: ComboCard,
        handCards: List<ComboCard>
    ) {
        TODO("Not yet implemented")
    }

    override fun id()=25072601

    override fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        groupId = cardWeightInfoList.firstOrNull()?.groupId ?:run {
            throw ConditionException("必须需要")
        }
    }
}

