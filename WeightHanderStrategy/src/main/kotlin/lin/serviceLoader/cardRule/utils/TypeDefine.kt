package lin.serviceLoader.cardRule.utils

import lin.bean.ComboCard
import lin.serviceLoader.cardRule.DepByWeightInfo
import lin.serviceLoader.cardRule.DepByWeightInfos

//
typealias DepToPredicates = (List<ComboCard>) -> Boolean

typealias DepToPredicate = (ComboCard) -> Boolean

interface DepByWeightInfoDelegate<T>: DepByWeightInfo{
    var depToPredicate :T
}

interface DepByWeightInfoDelegates<T> : DepByWeightInfos {
    var depToPredicate: T
}