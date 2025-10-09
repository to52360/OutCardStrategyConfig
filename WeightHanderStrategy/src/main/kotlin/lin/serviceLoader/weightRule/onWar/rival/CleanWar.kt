package lin.serviceLoader.weightRule.onWar.rival

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.combo.ComboParse.Companion.FirstUseGroupId
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.strategy.UseBeforeStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.serviceLoader.weightRule.onWar.rival.utils.DamageCache
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.CleanWarUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class CleanWar(val useGroupId: Int) : AbsWeightCondition(), KoinComponent, UseBeforeStrategy {
    companion object {
        //无伤害视为全部清理
        const val ALL_CLEAN: Int = 0
    }

    protected val cleanWarUtils: CleanWarUtils = get<CleanWarUtils>()
    protected val cache = DamageCache()
    protected val useBeforeStrategy: MutableList<UseBeforeStrategy> by lazy { mutableListOf(this) }
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val weight = calWeight(callCard)
        if (weight + callCard.powerWeight > NotWeight) {
            callCard.useBeforeStrategy?.add(this) ?: run { callCard.useBeforeStrategy = useBeforeStrategy }
            callCard.useGroupId = useGroupId
        }
        return weight
    }

    override fun extAction(
        comboCard: ComboCard,
        useStrategyUtils: UseStrategyUtils,
        warInfo: WarInfo
    ) {
        cleanWarUtils.reload()
        if (calWeight(comboCard) == UnUseWeight) {
            comboCard.unUse()
        }
    }

    abstract fun calWeight(callCard: ComboCard): Double
}

class DepNumRelWar : CleanWar(FirstUseGroupId) {
    override fun calWeight(callCard: ComboCard): Double {
        if (cleanWarUtils.compareRivalNum(number)) return UnUseWeight
        val damage = cache.getDamageById(callCard)
        if (damage == ALL_CLEAN) return groupWeight
        val cutWeight = unConditionWeight * cleanWarUtils.unPassRate(damage)
        return groupWeight + cutWeight
    }

    override fun description(): String {
        return "单向解场"
    }


}