package lin.d

import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import lin.ComboStrategyConfigFactory
import lin.Strategy.ComboStrategy
import lin.bean.v1.ComboWeightInfo
import lin.util.StrategyUtil
import lin.util.StrategyUtil.Entry

/**
 * todo 名字还没有想好
 * Entry(cardId: String,value: CardWeight)
 *
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */
class ComboWeightGroup(weightConfigs: MutableList<Entry>) {
    //存储转化权重信息
    private val infoMap : Map<String, ComboWeightInfo>
    //存储策略分组
    private val strategyMap : Map<Int, ComboStrategy>
    init{
        if(weightConfigs.isNotEmpty()) {

            infoMap = weightConfigParse(weightConfigs)
            // 步骤 D: 按“策略分组” (e.g., 16) 创建 strategyMap
            this.strategyMap = weightConfigs
                .map { it.value.weight.toInt() } // 获取所有策略ID
                .distinct() // 去重
                .associateWith { strategyId -> ComboStrategyConfigFactory.create(strategyId)!! }
        }else{
            this.infoMap = emptyMap()
            this.strategyMap = emptyMap()
        }
    }

    /**
     * 没有权重信息或者没有匹配对应策略
     */
    private fun hasValidStrategy() : Boolean{
        return infoMap.isNotEmpty() && strategyMap.isNotEmpty()
    }

    /**
     * 返回出牌策略,给策略类
     */
    fun getOutCardLambda() : (War)-> Unit{
        return if (hasValidStrategy()) {
            // 如果配置有效，返回我们精心设计的执行逻辑
            { war -> executeOutCardStrategy(war) }
        } else {
            // 如果配置无效，返回默认的激进策略
            log.info { "配置无效或为空，执行默认激进策略..." }
            defaultOutCardLambda
        }
    }
    //异常情况调用

    private fun executeOutCardStrategy(war :War) {

    }

}
private val  defaultStrategy:HsRadicalDeckStrategy by lazy {
    HsRadicalDeckStrategy()
}
//默认策略
val defaultOutCardLambda : (War)-> Unit={
    defaultStrategy.executeOutCard()
}
//将外部配置属性转化为符合内部项目格式
private val weightConfigParse: (MutableList<Entry>) ->Map<String, ComboWeightInfo> = {
        weightConfigs->
            // 步骤 A: 按“组合分组” (e.g., 16.1, 16.2) 进行分组
            val comboSubGroups = weightConfigs.groupBy { it.value.weight }

            // 步骤 B: 计算出每个“组合分组”中的最大 powerWeight
            val maxPowerWeightPerSubGroup = comboSubGroups.mapValues { (_, weights) ->
                weights.maxOf { it.value.powerWeight }
            }

            // 步骤 C: 创建 infoMap。这次的转化逻辑更丰富
             weightConfigs.associateBy(
                keySelector = { it.key }
            ) { weightCard ->
                // 获取该卡所属组合分组的最大powerWeight
                val maxPowerInSubGroup = maxPowerWeightPerSubGroup[weightCard.value.weight]!!

                ComboWeightInfo(
                    cardId = weightCard.key,
                    powerWeight = weightCard.value.powerWeight,
                    groupId = weightCard.value.weight,
                    strategyId = weightCard.value.weight.toInt(),
                    // 如果自己的powerWeight等于分组最大值，那么它就是核心
                    isCore = weightCard.value.powerWeight == maxPowerInSubGroup
                )
            }

}
