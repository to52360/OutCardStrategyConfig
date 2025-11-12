package lin.config.find.def

import lin.bean.CardWeightInfo
import kotlin.reflect.KClass

interface WeightInfoFinder<T : Any> {
    val targetType: KClass<T>

    fun process(key: T): List<CardWeightInfo>
}
