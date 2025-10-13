package lin.domain.strategy

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.AwaitAnimationTime
import lin.myLog


sealed interface UseStrategy

interface UseAfterStrategy : UseStrategy {
    fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo)
}

interface UseBeforeStrategy : UseStrategy {
    fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo)
}

object UseAfterLClick : UseAfterStrategy {
    override fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        myLog.info { "等待地标动画" }
        Thread.sleep(AwaitAnimationTime)
        comboCard.card.action.lClick()
        myLog.info { "再点一下" }
        Thread.sleep(AwaitAnimationTime)
        comboCard.card.action.lClick()
    }
}

/**
 *发现处理策略
 */
object DiscoverUseStrategy : UseAfterStrategy, UseBeforeStrategy {
    override fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        useStrategyUtils.register()
    }

    override fun afterExtAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        useStrategyUtils.await()
    }

}



