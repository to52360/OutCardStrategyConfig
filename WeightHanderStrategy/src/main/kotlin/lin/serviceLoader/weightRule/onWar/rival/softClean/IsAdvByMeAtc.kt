package lin.serviceLoader.weightRule.onWar.rival.softClean

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.isAdvByMeAtc

class IsAdvByMeAtc : AbsWeightCondition() {
    override fun description(): String {
        return "战场是否有优势,还参考我方攻击力"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val warStatus = warInfo.warStatus
        return if (warStatus.isAdvByMeAtc()) groupWeight
        else unConditionWeight
    }
}