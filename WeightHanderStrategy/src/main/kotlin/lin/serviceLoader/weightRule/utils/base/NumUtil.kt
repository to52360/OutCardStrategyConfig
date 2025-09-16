package lin.serviceLoader.weightRule.utils.base

import lin.domain.WarInfo
import lin.domain.context.CostWeight

class NumUtil<T>(val warInfo: WarInfo) {
    var weight: Double = CostWeight
    var firstPredicate: (WarInfo) -> Boolean = { true }
    var sorter: Comparator<T>? = null
    var filter: ((T) -> Boolean)? = null
    fun setWeight(weight: Double): NumUtil<T> {
        this.weight = weight
        return this
    }

    fun firstPredicate(predicate: (WarInfo) -> Boolean): NumUtil<T> {
        this.firstPredicate = predicate
        return this
    }



}

