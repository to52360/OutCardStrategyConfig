package lin.serviceLoader.weightRule

import lin.bean.ComboCard
import lin.domain.WarInfo

/**
 * 加权条件
 * todo-future id考虑用配置表
 */
interface WeightCondition : WeightRule, GroupWeight {
    //唯一
    fun id(): Int
    fun name(): String {
        return this.javaClass.simpleName
    }

    fun description() = name()
}

/**
 * todo-future 收起权重操作还在思考中
 */
interface AddWeightByCondition : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo) {
        callCard.addWeight(calculateWeight(warInfo))
    }

    fun calculateWeight(warInfo: WarInfo): Double
}

interface AddWeightByAll : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo) {
        val weight = calculateWeight(callCard, warInfo)
        callCard.addWeight(weight)
    }

    fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double
}


interface GroupWeight {
    var groupWeight: Double
}