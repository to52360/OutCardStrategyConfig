package lin.serviceLoader.cardRule

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.CardRule
import lin.serviceLoader.weightRule.hand.ChangeCardStrategy

/**
 * 玛克扎尔的小鬼
 */
class KAR089 : CardRule {
    val delegate = ChangeCardStrategy()
    override fun cardId(): String = "KAR_089"

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return delegate.calculateWeight(callCard, warInfo)
    }
}