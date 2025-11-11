package lin.config.find.def

import lin.bean.CardWeightInfo
import kotlin.reflect.KClass

interface WeightInfoFind<T : Any> {
    val targetType: KClass<T>

    fun process(key: T): List<CardWeightInfo>
}
