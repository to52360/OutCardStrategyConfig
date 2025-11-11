package lin.config.find

import lin.bean.CardWeightInfo
import lin.config.find.def.WeightInfoFind
import kotlin.reflect.KClass

class FindBy<T : Any>(kClass: KClass<T>, val processLambda: (T) -> List<CardWeightInfo>) : WeightInfoFind<T> {
    override val targetType: KClass<T> = kClass
    override fun process(key: T): List<CardWeightInfo> {
        return processLambda(key)
    }
}

inline fun <reified T : Any> findBy(
    crossinline processor: (T) -> List<CardWeightInfo>
): WeightInfoFind<T> = object : WeightInfoFind<T> {
    override val targetType: KClass<T> = T::class
    override fun process(key: T): List<CardWeightInfo> = processor(key)
}

class CardIdFind(val infoMap: Map<String, CardWeightInfo>) : WeightInfoFind<String> {
    override val targetType: KClass<String> = String::class
    override fun process(key: String): List<CardWeightInfo> {
        return listOfNotNull(infoMap[key])
    }
}


class CardIdFind1(val infoMap: Map<String, CardWeightInfo>) : WeightInfoFind<String>
by FindBy(String::class, { cardId -> listOfNotNull(infoMap[keys]) })