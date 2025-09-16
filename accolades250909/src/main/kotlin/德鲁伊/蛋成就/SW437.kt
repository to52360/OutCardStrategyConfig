package 德鲁伊.蛋成就

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.CardRule
import lin.serviceLoader.weightRule.hand.HandNum

class SW437 : CardRule {
    override fun cardId() = "SW_437"
    private val delegate: HandNum = HandNum(4)

    init {
        delegate.groupWeight = 5.0
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        return delegate.calculateWeight(callCard, warInfo)
    }
}