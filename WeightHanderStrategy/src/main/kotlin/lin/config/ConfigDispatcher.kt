package lin.config

import lin.bean.CardWeightInfo
import lin.config.handler.ConfigHandler
import kotlin.reflect.KClass

class ConfigDispatcher(
    handlers: List<ConfigHandler<*>>
) {

    private val handlerMap: Map<KClass<*>, ConfigHandler<*>> =
        handlers.associateBy { it.configType }

    fun <T : CardConfig> getHandler(configType: KClass<T>): ConfigHandler<T>? {
        @Suppress("UNCHECKED_CAST")
        return handlerMap[configType] as? ConfigHandler<T>
    }

    fun dispatch(cardConfigs: List<CardConfig>, cardWeightInfos: List<CardWeightInfo>) {
        val grouped = cardConfigs
            .filterIsInstance<BaseConfig>()
            .groupBy { config ->

            }
    }
}