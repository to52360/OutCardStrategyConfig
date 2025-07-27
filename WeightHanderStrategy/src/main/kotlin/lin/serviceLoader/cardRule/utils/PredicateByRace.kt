package lin.serviceLoader.cardRule.utils

import lin.bean.CardWeightInfo


class PredicateByRace : DepByWeightInfoDelegate<DepToPredicates> {
    //todo 存在被修改的风险
    override lateinit   var depToPredicate: DepToPredicates
    override fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        depToPredicate = cardWeightInfoList.infoToHandRacePredicate()
    }
}