package lin.dao

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import lin.strategy.ComboStrategy

import lin.bean.v1.ComboWeightInfoV1
import java.util.*


/**
 * todo 名字还没有想好
 * Entry(cardId: String,value: CardWeight)
 *
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */
class ComboWeightGroup(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>) {
    //存储转化权重信息
    private val infoMap : Map<String, ComboWeightInfoV1>
    //存储策略分组
    private val strategyMap : Map<String, ComboStrategy>
    //判断是否正确初始化,没有权重信息或者没有匹配对应策略情况
    private var useAble = true
    init{
        if(weightConfigs.isNotEmpty()) {
            infoMap = parseConfigInfo(weightConfigs)
            strategyMap = loadStrategyInfo(infoMap)
        }else{
            infoMap = emptyMap()
            strategyMap = emptyMap()
            useAble = false
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
        return if (hasValidStrategy()&&useAble) {
            // 如果配置有效，返回我们精心设计的执行逻辑
            { war -> executeOutCardStrategy(war) }
        } else {
            // 如果配置无效，返回默认的激进策略
            log.info { "配置无效或为空，执行默认激进策略..." }
            defaultOutCardLambda
        }
    }

    private fun executeOutCardStrategy(war :War) {

    }

    /**
     * 加载策略信息
     */
    private fun loadStrategyInfo(infoMap: Map<String, ComboWeightInfoV1>) :  Map<String, ComboStrategy>{
        val comboStrategyList: ServiceLoader<ComboStrategy> = ServiceLoader.load(ComboStrategy::class.java)
        val readInfo = hashMapOf<String, ComboStrategy>()
        val strategyGroup  = infoMap.values.groupBy{it.outCardStrategyId}
        for (comboStrategy in comboStrategyList) {
                readInfo[comboStrategy.id()] = comboStrategy
        }
        val cache = hashMapOf<String, ComboStrategy>()
        strategyGroup.keys.forEach {
            readInfo[it]?.let{
                result->
                cache.put(it, result)
            }?:run{
                log.info { "策略ID不存在，已禁用: $it" }
                useAble = false
            }
        }
        return cache
    }
    //把魔数转化为系统数据
    private fun parseConfigInfo(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>):Map<String, ComboWeightInfoV1> {
        return  weightConfigs.associateBy(
            keySelector = { it.key }
        ) { weightCard ->
            ComboWeightInfoV1(
                weightCard.value.weight.toInt(),
                DoubleSplitUtils.decimalInt(weightCard.value.weight),
                weightCard.value.powerWeight.toInt(),
                DoubleSplitUtils.decimalStr(weightCard.value.powerWeight)
            )
        }
    }

}

private val  defaultStrategy:HsRadicalDeckStrategy by lazy {
    HsRadicalDeckStrategy()
}
//默认策略
val defaultOutCardLambda : (War)-> Unit={
    defaultStrategy.executeOutCard()
}

