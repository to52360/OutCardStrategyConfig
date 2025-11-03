package lin.config


import lin.domain.use.UseStrategy

sealed interface BaseConfig : CardConfig

data class UseConfig(
    val useGroupId: Int? = null,
    val useGroupOrder: Double? = null,
    val useStrategyList: List<UseStrategy> = emptyList()
) : BaseConfig

// 卡牌类型配置接口


interface CardType : BaseConfig