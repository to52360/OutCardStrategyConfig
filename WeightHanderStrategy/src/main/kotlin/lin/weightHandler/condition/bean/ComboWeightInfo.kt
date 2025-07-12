package lin.weightHandler.condition.bean


/**
 * [club.xiaojiawei.bean.CardWeight]
 */
data class ComboWeightInfo(
    val cardId: String,
    val groupId: Double,
    val powerWeight: Double

){

    var metadata: MutableMap<MetadataKey<*>, Any> ? = null
    fun <T> putMetadata(key: MetadataKey<T>, value: T) {
        if (metadata != null) {
            metadata!![key] = value as Any
        } else {
            metadata = mutableMapOf(key to value as Any)
        }

    }

}

@JvmInline
value class MetadataKey<T>(val name: String)


