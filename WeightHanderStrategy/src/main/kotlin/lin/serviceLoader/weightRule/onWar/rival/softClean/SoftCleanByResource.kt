package lin.serviceLoader.weightRule.onWar.rival.softClean

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.WarStatus
import lin.serviceLoader.weightRule.utils.war.acceptableRivalAttack
import lin.serviceLoader.weightRule.utils.war.isAdvByMeAtc
import lin.warExt.my.base.resource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SoftCleanByResource : AbsWeightCondition(), KoinComponent {
    val warStatus: WarStatus = get<WarStatus>()
    override fun description(): String {
        return "包含血量加权,写死在代码里"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val ableAtc = acceptableRivalAttack(warInfo.resource())
        warStatus.reLoadOnce()

        if (warStatus.isAdvByMeAtc(ableAtc)) {
            return unConditionWeight
        }
        var excessDamageFactor = warStatus.excessDamage / ableAtc
        val maxFactor = 2//避免太大的倍率
        if (excessDamageFactor > maxFactor) excessDamageFactor = maxFactor

        return groupWeight * excessDamageFactor

    }


}