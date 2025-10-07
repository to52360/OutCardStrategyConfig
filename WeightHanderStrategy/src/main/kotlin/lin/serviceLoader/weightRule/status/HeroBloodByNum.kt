package lin.serviceLoader.weightRule.status

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.hero

class HeroBloodByNum : AbsWeightCondition() {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val blood = warInfo.hero()?.blood() ?: 0
        return if (blood > number) {
            groupWeight
        } else return unConditionWeight


    }
}