package lin.weightHandler.condition.bean


/**
 * [club.xiaojiawei.bean.CardWeight]
 */
class ComboWeightInfo(
    val cardId: String,
    val groupId: Double,
    val powerWeight: Double

){
    val metadata: Metadata by lazy { Metadata() }
}

@JvmInline
value class MetadataKey<T>(val name: String)


@JvmInline
value class Metadata(private val map: MutableMap<MetadataKey<*>, Any> = mutableMapOf()) {

    fun <T> put(key: MetadataKey<T>, value: T) {
        map[key] = value as Any
    }

    fun <T> get(key: MetadataKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as? T
    }
}