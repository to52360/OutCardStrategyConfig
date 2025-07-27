package lin.serviceLoader.cardRule.utils

import lin.bean.CardWeightInfo

/**
 * 委托存在指定组判断函数
 */
class PredicateByGroup : DepByWeightInfoDelegates<DepToPredicates> {
    override lateinit var depToPredicate: DepToPredicates

    override fun initByWeightInfos(cardWeightInfoList: List<List<CardWeightInfo>>) {
        val option = cardWeightInfoList.map { it.first().groupId }
        depToPredicate = { canUseCard ->
            option.any { groupId -> canUseCard.any { groupId == it.groupId() } }
        }
    }
}
