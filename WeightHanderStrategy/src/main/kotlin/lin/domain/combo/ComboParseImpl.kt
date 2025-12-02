package lin.domain.combo

import lin.bean.*


class LastUseCombo : ValidBindComboParse {

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

    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>, comboInfo: ComboInfo
    ) {
        cardWeightInfos.forEach {
            it.useGroupId = FirstUseGroupId
            it.useGroupOrder = comboInfo.comboWeight
        }
    }

}

/**
 *同组加权
 */
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

