package lin.domain.combo

import lin.bean.*
import lin.domain.context.NotWeight

class LastUseCombo : ValidBindComboParse {
    override fun id(): String {
        return "last"
    }

    override fun processBindCombo(
        cardWeightInfos: List<CardWeightInfo>, comboInfo: ComboInfo
    ) {
        cardWeightInfos.forEach {
            it.lastUse = LastUse(comboInfo.comboWeight)
        }
    }

}

interface ComboPredicateByGroup {
    fun predicateByGroup(comboInfo: ComboInfo): ComboRule {
        val comboRule: ComboRule = { comboCards ->
            if (comboInfo.depIds.any {
                    it == comboCards.groupId()
                })
                comboInfo.comboWeight
            else
                NotWeight
        }
        return comboRule
    }
}

open class ComboImpl : ValidDepComboParse, ComboPredicateByGroup {
    override fun id() = "def"
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

