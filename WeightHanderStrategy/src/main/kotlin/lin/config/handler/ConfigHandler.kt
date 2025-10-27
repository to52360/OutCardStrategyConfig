package lin.config.handler

import lin.bean.CardWeightInfo
import lin.config.BaseConfig
import lin.config.CardConfig
import lin.config.UseConfig
import kotlin.reflect.KClass

interface ConfigHandler<T : CardConfig> {
    val configType: KClass<out T>
    fun processConfig(cardConfigs: List<T>, cardWeightInfos: List<CardWeightInfo>)
}

class UseConfigHandler : ConfigHandler<BaseConfig> {
    override val configType: KClass<out BaseConfig> = BaseConfig::class
    override fun processConfig(cardConfigs: List<BaseConfig>, cardWeightInfos: List<CardWeightInfo>) {
        cardConfigs.forEach { config ->
            when (config) {
                is UseConfig -> {
                    //使用策略存在重复添加问题
                    cardWeightInfos.forEach { info ->
                        config.useGroupId?.let { info.useGroupId = it }
                        config.useGroupOrder?.let { info.useGroupOrder = it }
                        config.useStrategyList.forEach {
                            info.addUseStrategy(it)
                        }
                    }
                }
            }
        }
    }
}