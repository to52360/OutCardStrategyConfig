package lin.serviceLoader.weightRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight

/**
 * 加权条件
 * 应该权重处理之后
 * [lin.weightHandler.condition.bean.ConditionGroup]
 * 可选接口
 * [DepWeightInfo]依赖
 * [lin.lifecycle.RoundLifecycle] 生命周期
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
 * todo-future 看有没有必要,还没有考虑实现方案 之后权重处理器
 * 1.融合在ConditionWeightHandler能快速发现,语义和扩展会有问题
 */
interface AfterWeightCondition : WeightCondition

abstract class AbstractWeightRule : WeightCondition {
    override var groupWeight: Double = CostWeight

}

/**
 * todo-future 收起权重操作还在思考中
 */
@Suppress("ConstantConditionIf")
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