package drop.cardRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.CardRule
import lin.serviceLoader.weightRule.hand.ChangeCardStrategy
import org.koin.core.component.KoinComponent

/**
 * 玛克扎尔的小鬼
 */
class KAR089 : CardRule, KoinComponent {
    val delegate = ChangeCardStrategy()

    init {
        delegate.setNum(7)
        delegate.setUnCondWeight(0.0)
    }
    override fun cardId(): String = "KAR_089"

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {

        return delegate.calculateWeight(callCard, warInfo)
    }
}