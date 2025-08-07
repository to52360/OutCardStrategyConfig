package lin.serviceLoader.weightRule.utils

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.DepByWeightGroupId
import lin.serviceLoader.weightRule.DepByWeightInfo
import lin.serviceLoader.weightRule.DepByWeightInfos

/**
 * 委托实现的接口
 */
//
typealias DepToPredicates = (List<ComboCard>) -> Boolean

typealias DepToPredicate = (ComboCard) -> Boolean

/**
 * 依赖单个
 */
interface DepByWeightInfoDelegate<T>: DepByWeightInfo{
    var depToPredicate :T
}

/**
 * 依赖多个
 */
interface DepByWeightInfoDelegates<T> : DepByWeightInfos {
    var depToPredicate: T
}

/**
 * 依赖分组只
 */
interface DepByWeightGroupIdDelegate<T> : DepByWeightGroupId {
    var depToPredicate :T
}