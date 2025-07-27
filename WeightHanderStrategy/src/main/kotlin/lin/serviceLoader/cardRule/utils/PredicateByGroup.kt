package lin.serviceLoader.cardRule.utils

import lin.bean.CardWeightInfo

class PredicateByGroup : DepByWeightInfoDelegates<DepToPredicates> {
    override lateinit var depToPredicate: DepToPredicates

    override fun initByWeightInfo(cardWeightInfoList: List<List<CardWeightInfo>>) {
        val option = cardWeightInfoList.map { it.first().groupId }
        depToPredicate = { canUseCard ->
            option.any { groupId -> canUseCard.any { groupId == it.groupId() } }
        }
    }
}
