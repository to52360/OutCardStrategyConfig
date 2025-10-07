package lin.serviceLoader.weightRule.onWar.buff

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition

abstract class PointWarCard(val option: (ComboCard) -> Boolean) : AbsWeightCondition() {
    override fun description(): String {
        return "指向指定目标,多个指向费用最高的"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val comboCard = warInfo.playComboCards.filter { option(it) }.maxByOrNull { it.cost() }
        comboCard?.run {
            callCard.pointCard = this.card
            return groupWeight
        }
        return unConditionWeight
    }
}

class PointDeathRattleCard : PointWarCard({ comboCard ->
    comboCard.card.isDeathRattle
})