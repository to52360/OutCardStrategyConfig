package lin.serviceLoader.weightRule.utils

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.DepByWeightGroupId
import lin.serviceLoader.weightRule.DepWeightInfo

/**
 * 委托实现的接口
 */
//
typealias DepToPredicateList = (List<ComboCard>) -> Boolean

typealias DepToPredicate = (ComboCard) -> Boolean

/**
 * 依赖数据只有一个
 */
interface DepWeightInfoDelegate<T> : DepWeightInfo {
    var depInfoToPredicate: T
}

/**
 * 依赖多个
 */
interface MoreDepByWeightInfoDelegates<T> : DepWeightInfo {
    var moreDepToPredicate: T
}

/**
 * 依赖分组
 */
interface DepWeightGroupDelegate<T> : DepByWeightGroupId {
    var depToPredicate :T
}