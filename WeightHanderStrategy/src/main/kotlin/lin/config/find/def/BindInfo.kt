package lin.config.find.def

import lin.config.CardConfig

/**
 * 多个条件同一配置
 * @param findKey 只能同一类型
 */
data class BindInfo(val findKey: List<Any>, val cardConfigs: List<CardConfig>) {
    constructor(key: Any, vararg cardConfig: CardConfig) : this(listOf(key), cardConfig.toList())
}


