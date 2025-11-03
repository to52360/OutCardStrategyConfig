package lin.serviceLoader.weightRule.onWar.rival.softClean

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.ONE_FACTOR
import lin.serviceLoader.weightRule.utils.war.excessDamageFactor

class ExcessDamageFactorByNum : AbsWeightCondition() {

    override fun setNum(num: Int) {
        number = num * ONE_FACTOR
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val warStatus = warInfo.warStatus
        val excessDamageFactor = warStatus.excessDamageFactor()
        val offer = ONE_FACTOR / 2  //区间偏移量,随便写的
        if (excessDamageFactor < number - offer) return unConditionWeight
        return if (excessDamageFactor < number + offer) groupWeight
        else groupWeight / 2
        /*{
        //todo-future 临时算法,暂时想出的方案
        if(unConditionWeight==NotWeight) return NotWeight
        return groupWeight-unConditionWeight
    }*/
    }
}