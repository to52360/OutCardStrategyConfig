package lin.domain.combo

import lin.bean.CardWeightInfo
import lin.bean.ComboInfo
import lin.bean.ComboRule

class ChangeComboParse : ValidDepComboParse, ComboPredicateByGroup {
    override fun id() = "change"
    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>,
        comboInfo: ComboInfo
    ) {
        val comboRule: ComboRule = predicateByGroup(comboInfo)
        cardWeightInfos.forEach { it.addChangeComboRule(comboRule) }
    }
}

