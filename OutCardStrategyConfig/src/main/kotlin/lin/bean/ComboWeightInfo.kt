package lin.bean


/**
 * [club.xiaojiawei.bean.CardWeight]
 * todo 暂定 不知道是否需要考虑
 */
class ComboWeightInfo(
    val cardId: String,
    val groupId: Double,
    val powerWeight: Double

){
    val metadata: Metadata by lazy { Metadata() }
}

class MetadataKey<T>(val name: String)

class Metadata {
    private val map = mutableMapOf<MetadataKey<*>, Any>()

    fun <T> put(key: MetadataKey<T>, value: T) {
        map[key] = value as Any
    }

    fun <T> get(key: MetadataKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as? T
    }
}