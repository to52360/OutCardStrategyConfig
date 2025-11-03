package lin.domain.use

import lin.bean.ComboCard
import lin.domain.context.AwaitAnimationTime
import lin.myLog


sealed interface UseStrategy

interface UseAfterStrategy : UseStrategy {
    fun afterExtAction(comboCard: ComboCard, useDomain: UseDomain)
}

interface UseBeforeStrategy : UseStrategy {
    fun extAction(comboCard: ComboCard, useDomain: UseDomain)
}

object UseAfterLClick : UseAfterStrategy {
    override fun afterExtAction(comboCard: ComboCard, useDomain: UseDomain) {
        myLog.info { "等待地标动画" }
        Thread.sleep(AwaitAnimationTime)
        comboCard.card.action.lClick()
        useDomain.register()
        useDomain.await()
        //避免没点到
        myLog.info { "再点一下" }
        Thread.sleep(AwaitAnimationTime)
        comboCard.card.action.lClick()
    }
}

/**
 *发现处理策略
 */
object DiscoverUseStrategy : UseAfterStrategy, UseBeforeStrategy {
    override fun extAction(comboCard: ComboCard, useDomain: UseDomain) {
        useDomain.register()
    }

    override fun afterExtAction(comboCard: ComboCard, useDomain: UseDomain) {
        useDomain.await()
    }

}
object AwaitAnimationStrategy : UseAfterStrategy {
    override fun afterExtAction(
        comboCard: ComboCard,
        useDomain: UseDomain
    ) {
        useDomain.extAwait = AwaitAnimationTime
    }

}



