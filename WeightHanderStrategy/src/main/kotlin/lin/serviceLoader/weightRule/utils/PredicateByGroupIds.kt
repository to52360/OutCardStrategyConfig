package lin.serviceLoader.weightRule.utils

/**
 * 委托存在指定组判断函数
 * 存在指定分组卡牌
 */
class PredicateByGroupIds : DepByWeightGroupIdDelegate<DepToPredicates> {
    override lateinit var depToPredicate: DepToPredicates


    override fun initByGroupIds(groupIds: Array<Double>) {
        depToPredicate = { canUseCard ->
            groupIds.any { groupId -> canUseCard.any { groupId == it.groupId() } }
        }
    }
}

class PredicateByGroupId : DepByWeightGroupIdDelegate<DepToPredicate> {
    override lateinit var depToPredicate: DepToPredicate


    override fun initByGroupIds(groupIds: Array<Double>) {
        depToPredicate = { canUseCard ->
            groupIds.any { canUseCard.groupId() == it }
        }
    }
}
