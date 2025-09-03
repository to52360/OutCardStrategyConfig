package lin.serviceLoader.weightRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight

/**
 * 加权条件
 * 应该权重处理之后
 * [lin.weightHandler.condition.bean.ConditionGroup]
 */
interface WeightCondition : WeightRule, GroupWeight {
    //唯一
    override fun id(): String {
        return name()
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
interface AddWeightByWarInfo : WeightCondition {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return calculateWeight(warInfo)
    }
    fun calculateWeight(warInfo: WarInfo): Double
}

/**
 * todo-future 加个不符合要求权重值
 */
interface GroupWeight {
    var groupWeight: Double
    fun setUnCondWeight(unConditionWeight: Double) {

    }

}