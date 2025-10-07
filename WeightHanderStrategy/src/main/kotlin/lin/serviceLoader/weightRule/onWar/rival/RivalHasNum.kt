package lin.serviceLoader.weightRule.onWar.rival

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getPlayCards

abstract class RivalHasNum : AbsWeightCondition() {
    override fun description(): String {
        return "对手有数量指定值加权"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val result = warInfo.getPlayCards().any { it.canHurt() && cardToNumber(it) > number }
        return if (result) groupWeight
        else unConditionWeight
    }

    abstract fun cardToNumber(card: Card): Int

}

class RivalHasActByNum : RivalHasNum() {
    override fun cardToNumber(card: Card): Int {
        return card.atc
    }
}

class RivalHasBloodByNum : RivalHasNum() {
    override fun cardToNumber(card: Card): Int {
        return card.blood()
    }
}

class RivalHasBloodOrActByNum : RivalHasNum() {
    override fun cardToNumber(card: Card): Int {
        if (card.blood() > card.atc) return card.blood()
        return card.atc
    }
}