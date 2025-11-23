package lin.config.find.def

import lin.config.CardConfig

/**
 * 多个条件同一配置
 * @param findKey 只能同一类型
 */
data class BindInfo(val findKey: List<Any>, val cardConfigs: List<CardConfig>)

fun singleBindCardId(cardId: String, cardConfig: CardConfig): BindInfo {
    return BindInfo(listOf(cardId), listOf(cardConfig))
}

