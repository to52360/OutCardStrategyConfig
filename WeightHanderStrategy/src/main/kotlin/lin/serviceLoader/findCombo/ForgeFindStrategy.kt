package lin.serviceLoader.findCombo

import lin.domain.strategy.FindComboStrategy.Companion.EXT_COST_PRIORITY
import lin.domain.strategy.FindPlanner
import lin.domain.strategy.FindRule
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getHandCards

/**
 * 锻造用不了
 */
class ForgeFindStrategy : AbsFindStrategy(FORGE_COST, FORGE_WEIGHT, { comboCard ->
    comboCard.card.isForge
}) {
    companion object {
        const val FORGE_COST = -2
        const val FORGE_WEIGHT = 5.0
    }

    val forgeRule: FindRule = { comboCard ->
        comboCard.card.isForge
    }

    override fun priority() = EXT_COST_PRIORITY + 1 //比额外费用后


    override fun isExecute(findPlanner: FindPlanner): Boolean {
        val warManage = findPlanner.warManage
        val canUseCards = warManage.canUseCards
        return !canUseCards.any(forgeRule) && warManage.getCost() >= 2
    }


    override fun emptyResultAction(findPlanner: FindPlanner) {
        val handCards = findPlanner.warManage.getHandCards()
        val forgeCards = handCards.filter { it.isForge }.minBy { it.cost }
        forgeCards.action.forge(true)
    }

}