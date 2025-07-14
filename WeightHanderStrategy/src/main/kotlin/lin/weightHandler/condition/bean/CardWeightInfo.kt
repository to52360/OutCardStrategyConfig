package lin.weightHandler.condition.bean


/**
 * @param groupId 使用weight的值 [club.xiaojiawei.bean.CardWeight.weight]
 */
data class CardWeightInfo(
    val cardId: String,
    val powerWeight: Double,
    val groupId: Double = 0.0,
    val cardType: CardType = CardType.DEFAULT
){
    //元数据 用来存储
    val metadata: MutableMap<MetadataKey<*>, Any>  = mutableMapOf()
    fun <T> putMetadata(key: MetadataKey<T>, value: T) {
        metadata[key] = value as Any

    }

}

@JvmInline
value class MetadataKey<T>(val name: String)

enum class CardType {
    DEFAULT,             //默认值无意义
    CHANGE,               //改变手牌
    ADD_COST,         //增加费用的
}

val AddCost by lazy { MetadataKey<Int>("AddCost") }
