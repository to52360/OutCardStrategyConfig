package lin

import lin.strategy.ComboStrategy
import java.util.*
import kotlin.collections.Map

/**
 * 策略获取类
 * 参考club.xiaojiawei.hsscript.status.PluginManager.loadPlugin
 */
object ComboStrategyConfigFactory {
    //暂时无法根据需求情况加载策略
    private val strategyMap = hashMapOf<String, ComboStrategy>()
    init {

    }

    fun get(strategyId:String) : ComboStrategy? {
        if(strategyMap.isEmpty()) return null
        return strategyMap[strategyId]
    }
    fun loadConfig(){

    }
}