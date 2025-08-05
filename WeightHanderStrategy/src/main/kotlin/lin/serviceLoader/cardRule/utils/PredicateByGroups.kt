package lin.serviceLoader.cardRule.utils

/**
 * 委托存在指定组判断函数
 */
class PredicateByGroups : DepByWeightGroupDelegate<DepToPredicates> {
    override lateinit var depToPredicate: DepToPredicates


    override fun initByGroupIds(groupIds: Array<Double>) {
        depToPredicate = { canUseCard ->
            groupIds.any { groupId -> canUseCard.any { groupId == it.groupId() } }
        }
    }
}
class PredicateByGroup : DepByWeightGroupDelegate<DepToPredicate> {
    override lateinit var depToPredicate: DepToPredicate


    override fun initByGroupIds(groupIds: Array<Double>) {
        depToPredicate = { canUseCard ->
            groupIds.any { canUseCard.groupId() == it }
        }
    }
}
