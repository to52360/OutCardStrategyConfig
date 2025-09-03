package lin.domain.combo

import lin.bean.*

class LastUseCombo : ValidBindComboParse {

    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>, comboInfo: ComboInfo
    ) {
        cardWeightInfos.forEach {
            it.lastUse = LastUse(comboInfo.comboWeight)
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

