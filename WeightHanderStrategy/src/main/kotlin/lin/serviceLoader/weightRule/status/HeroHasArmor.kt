package lin.serviceLoader.weightRule.status

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.getArmor
import lin.warExt.my.base.hasArmor
import lin.warExt.my.base.hero

class HeroHasArmor : AbsWeightCondition() {
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        if (warInfo.hasArmor()) return groupWeight
        return unConditionWeight
    }
}

class HeroWeightArmor : AbsWeightCondition() {
    override fun description(): String {
        return ""
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        if (getArmor(warInfo.hero()) > number) {
            return groupWeight
        }
        return unConditionWeight
    }
}