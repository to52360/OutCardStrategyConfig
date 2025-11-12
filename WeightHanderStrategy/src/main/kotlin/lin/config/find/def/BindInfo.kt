package lin.config.find.def

import lin.config.CardConfig

/**
 * 多个条件同一配置
 */
data class BindInfo(val findKey: List<Any>, val cardConfigs: List<CardConfig>)