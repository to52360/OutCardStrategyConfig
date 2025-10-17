package lin.serviceLoader.weightRule.onWar.rival

import lin.bean.ComboCard
import lin.bean.DefUseGroupId
import lin.config.CardConfig
import lin.config.UseConfig
import lin.domain.WarInfo
import lin.domain.context.ChangeAnimationTime
import lin.domain.context.UnUseWeight
import lin.domain.strategy.UseAfterStrategy
import lin.domain.strategy.UseBeforeStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.serviceLoader.weightRule.ExtConfig
import lin.serviceLoader.weightRule.onWar.rival.utils.DamageCache
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.CleanWarUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class CleanWar(val useGroupId: Int) : AbsWeightCondition(), KoinComponent, UseBeforeStrategy, ExtConfig,
    UseAfterStrategy {
    companion object {
        //无伤害视为全部清理
        const val ALL_CLEAN: Int = 0
    }

    protected val cleanWarUtils: CleanWarUtils = get<CleanWarUtils>()
    protected val cache = DamageCache()
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val weight = calWeight(callCard)
        return weight
    }

    override fun cardConfigs(): List<CardConfig> {
        return listOf(UseConfig(useGroupId, useStrategyList = listOf(this)))
    }

    override fun extAction(
        comboCard: ComboCard,
        useStrategyUtils: UseStrategyUtils,
        warInfo: WarInfo
    ) {
        //todo 存在重复调用问题,牺牲性能获取简单正确实现
        cleanWarUtils.reload()
        if (calWeight(comboCard) == UnUseWeight) {
            comboCard.unUse()
        }
    }

    override fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        Thread.sleep(ChangeAnimationTime)
    }

    abstract fun calWeight(callCard: ComboCard): Double
}

class DepNumRelWar : CleanWar(DefUseGroupId) {
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