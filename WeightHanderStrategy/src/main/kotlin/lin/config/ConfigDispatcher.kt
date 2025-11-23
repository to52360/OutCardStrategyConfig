package lin.config

import lin.bean.CardWeightInfo
import lin.config.find.def.BindInfo

import lin.config.find.def.WeightInfoFinder
import lin.config.handler.ConfigHandler
import lin.myLog
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

/**
 * todo-future 分管配置,过度版(存放还是原来位置,逐渐分离)
 */
class ConfigDispatcher(
    handlers: List<ConfigHandler<*>>,
    bindInfoFind: List<WeightInfoFinder<*>>
) : KoinComponent {

    private val handlerMap: Map<KClass<*>, ConfigHandler<*>> =
        handlers.associateBy { it.configType }
    private val bindInfoFindMap: Map<KClass<out Any>, WeightInfoFinder<out Any>> =
        bindInfoFind.associateBy { it.targetType }
    init {
        val bindInfos: List<BindInfo> = getKoin().getAll()
        bindInfos.forEach { bindInfo ->
            processUniformList(bindInfo.cardConfigs, bindInfo.findKey)
        }
    }

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

    /**
     *
     */
    private fun processUniformList(cardConfigs: List<CardConfig>, list: List<Any>) {
        val groupedByType: Map<KClass<out Any>, List<Any>> = list.groupBy { it::class }
        val cardWeightInfos = mutableListOf<CardWeightInfo>()
        groupedByType.forEach { (kClass, items) ->
            bindInfoFindMap[kClass]?.let {
                @Suppress("UNCHECKED_CAST")
                val handler = it as WeightInfoFinder<Any>
                items.forEach { item ->
                    {
                        val findResult = handler.process(item)
                        if (findResult.isEmpty()) {
                            myLog.warn { "key:${item} not find handler  " }
                        } else {
                            cardWeightInfos.addAll(findResult)
                        }
                    }
                }
            } ?: run {
                myLog.warn { "不支持类型: $kClass" }
            }
        }
        //todo-future 配置暂时还是放在一起,还没想好方案
        dispatch(cardConfigs, cardWeightInfos)

    }


}