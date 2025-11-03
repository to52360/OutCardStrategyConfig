package lin.config

import lin.bean.CardWeightInfo
import lin.config.handler.ConfigHandler
import lin.myLog
import kotlin.reflect.KClass

/**
 * 分管配置,过度版(存放还是原来位置,逐渐分离)
 */
class ConfigDispatcher(
    handlers: List<ConfigHandler<*>>
) {

    private val handlerMap: Map<KClass<*>, ConfigHandler<*>> =
        handlers.associateBy { it.configType }

    fun <T : CardConfig> getHandler(configType: KClass<T>): ConfigHandler<T>? {
        @Suppress("UNCHECKED_CAST")
        return handlerMap[configType] as? ConfigHandler<T>
    }

    /**
     * todo-future 暂时方案,不应该传cardWeightInfos,应该传什么还没想想清
     * 待定是分组List
     */
    fun dispatch(cardConfigs: List<CardConfig>, cardWeightInfos: List<CardWeightInfo>) {
        val buckets = handlerMap.keys.associateWith { mutableListOf<CardConfig>() }
        // 单次遍历 configs
        for (config in cardConfigs) {
            var matched = false
            for (groupType in handlerMap.keys) {
                if (groupType.java.isInstance(config)) {
                    //todo 存在不支持报错,导致运行
                    buckets[groupType]!!.add(config)
                    matched = true
                }
            }
            if (!matched) {
                myLog.warn { "No handler for: ${config::class.simpleName}" }
            }
            for ((groupType, group) in buckets) {
                if (group.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    (handlerMap[groupType] as ConfigHandler<CardConfig>).processConfig(group, cardWeightInfos)
                }
            }
        }
    }
}