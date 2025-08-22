package lin.domain.combo

import lin.bean.ComboCard
import lin.domain.context.AwaitAnimationTime
import lin.myLog


interface UseStrategy

interface UseAfterStrategy : UseStrategy {
    fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils)
}

interface UseBeforeStrategy : UseStrategy {
    fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils)
}

object UseAfterLClick : UseAfterStrategy {
    override fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils) {
        myLog.info { "等待地标动画" }
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



