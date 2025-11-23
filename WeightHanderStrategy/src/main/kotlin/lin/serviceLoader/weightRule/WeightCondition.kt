package lin.serviceLoader.weightRule

import lin.bean.ComboCard
import lin.config.CardConfig
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.myLog

/**
 * 加权条件
 * 应该权重处理之后
 * [lin.weightHandler.condition.bean.ConditionGroup]
 * 可选接口
 * [DepWeightInfo]依赖
 * [lin.lifecycle.RoundLifecycle] 生命周期
 * [ExtConfig]额外配置信息
 *
 */
interface WeightCondition : WeightRule, GroupWeight {
    //唯一
    override fun id(): String {
        return this.javaClass.simpleName
    }
    fun name(): String {
        return this.javaClass.simpleName
    }

    fun description() = name()
}

/**
 * 更多意图,兼用旧体系用的接口,且不用写WeightRule接口的calculateWeight实现
 * todo-future 过度方案,先测试可行性
 */
interface IntentRuleAsWeightRule : WeightRule, IntentRule {
    @Deprecated("Stub for compatibility. Always returns NotWeight.", level = DeprecationLevel.HIDDEN)
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        myLog.warn { "用于解决兼容问题,不该调用该方法" }
        return NotWeight
    }

    override val ruleId: String
        get() = id()
}

interface ExtConfig {
    fun cardConfigs(): List<CardConfig>
}

/**
 * todo-future 看有没有必要,还没有考虑实现方案 之后权重处理器
 * 之后执行
 * 1.融合在ConditionWeightHandler能快速发现,语义和扩展会有问题
 */
interface AfterWeightCondition : WeightCondition



/**
 * todo-future 减少信息的接口不知道需不需要了
 */
interface AddWeightByWarInfo : WeightCondition {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return calculateWeight(warInfo)
    }
    fun calculateWeight(warInfo: WarInfo): Double
}

/**
 * 依赖数据
 */
interface GroupWeight {
    var groupWeight: Double

    /**
     * 兼容写法
     */
    fun setUnCondWeight(unConditionWeight: Double) {

    }
    fun setNum(num: Int) {

    }

}