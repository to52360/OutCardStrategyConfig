package lin.serviceLoader.weightRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.myLog

/**
 * 加权条件
 * todo-future id考虑用配置表
 */
interface WeightCondition : WeightRule, GroupWeight {
    //唯一
    fun id(): String {
        return name()
    }
    fun name(): String {
        return this.javaClass.simpleName
    }

    fun description() = name()
}

/**
 * todo-future 收起权重操作还在思考中
 */
interface AddWeightByWarInfo : AddWeightByAllInfo {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return calculateWeight(warInfo)
    }


    fun calculateWeight(warInfo: WarInfo): Double
}

interface AddWeightByAllInfo : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo) {
        val weight = calculateWeight(callCard, warInfo)
        if (weight == UnUseWeight) {
            callCard.unUse()
        } else
        callCard.addWeight(weight)
        if (weight != NotWeight)
            myLog.info { "处理id:${id()},计算的权重权重:${weight},id:${callCard.cardId()},总权重:${callCard.powerWeight}" }
    }

    fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double
}


interface GroupWeight {
    var groupWeight: Double
}