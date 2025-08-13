package lin.bean

import lin.domain.UseStrategyUtils
import lin.weightHandler.condition.context.AwaitAnimationTime


interface UseStrategy

interface UseAfterStrategy : UseStrategy {
    fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils)
}

interface UseBeforeStrategy : UseStrategy {
    fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils)
}

object UseAfterLClick : UseAfterStrategy {
    override fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils) {
        Thread.sleep(AwaitAnimationTime)
        comboCard.card.action.lClick()
    }
}

/**
 *发现处理策略
 */
object DiscoverUseStrategy : UseAfterStrategy, UseBeforeStrategy {
    override fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils) {
        useStrategyUtils.register()
    }

    override fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils) {
        useStrategyUtils.await()
    }

}



