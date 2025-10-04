package lin.serviceLoader.weightRule.onWar.buff


import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getPlayCards

class OnWarNum : AbsWeightCondition() {



    override fun description(): String {
        return "根据随从增加权重"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val num = warInfo.getPlayCards().filter { it.canHurt() }.size
        return if (num > number) groupWeight else unConditionWeight
    }


}