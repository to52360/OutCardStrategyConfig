package lin.config.find

import lin.bean.CardWeightInfo
import lin.config.find.def.WeightInfoFinder
import kotlin.reflect.KClass


inline fun <reified T : Any> findBy(
    crossinline processor: (T) -> List<CardWeightInfo>
): WeightInfoFinder<T> = object : WeightInfoFinder<T> {
    override val targetType: KClass<T> = T::class
    override fun process(key: T): List<CardWeightInfo> = processor(key)
}

