package lin.domain.combo

import lin.bean.CardWeightInfo
import lin.bean.Combo
import lin.bean.ComboInfo
import lin.bean.ComboRule


class LastUseCombo : ValidBindComboParse {
    companion object {
        const val LastUseGroupId = 20
    }

    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>, comboInfo: ComboInfo
    ) {
        cardWeightInfos.forEach {
            it.useGroupId = LastUseGroupId
            it.useGroupOrder = comboInfo.comboWeight
        }
    }

}

class FirstUseCombo : ValidBindComboParse {
    companion object {
        const val FirstUseGroupId = 0
    }

    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>, comboInfo: ComboInfo
    ) {
        cardWeightInfos.forEach {
            it.useGroupId = FirstUseGroupId
            it.useGroupOrder = comboInfo.comboWeight
        }
    }

}


open class ComboImpl : ValidDepComboParse, ComboPredicateByGroup {
    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>,
        comboInfo: ComboInfo
    ) {
        val comboRule: ComboRule = predicateByGroup(comboInfo)
        val combo = Combo(comboInfo.infoId, comboRule, comboInfo.comboType)
        //赋值
        cardWeightInfos.forEach {
            it.addCombo(combo)
        }
    }

}

