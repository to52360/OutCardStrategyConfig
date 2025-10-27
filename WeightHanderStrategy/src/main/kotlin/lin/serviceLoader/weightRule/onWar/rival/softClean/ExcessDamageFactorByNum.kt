package lin.serviceLoader.weightRule.onWar.rival.softClean

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.ONE_FACTOR
import lin.serviceLoader.weightRule.utils.war.WarStatus
import lin.serviceLoader.weightRule.utils.war.excessDamageFactor

class ExcessDamageFactorByNum : AbsWeightCondition() {

    override fun setNum(num: Int) {
        number = num * ONE_FACTOR
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val warStatus = warInfo.warStatus
        val excessDamageFactor = warStatus.excessDamageFactor()
        val offer = ONE_FACTOR / 2 + 2 //区间偏移量,随便写的
        if (excessDamageFactor in number - offer until number + offer) return groupWeight
        return unConditionWeight
    }
}