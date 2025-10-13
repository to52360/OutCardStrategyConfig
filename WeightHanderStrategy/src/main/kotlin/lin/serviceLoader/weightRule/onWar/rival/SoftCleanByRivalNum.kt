package lin.serviceLoader.weightRule.onWar.rival

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.CleanWarUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SoftCleanByRivalNum : AbsWeightCondition(), KoinComponent {
    val cleanWarUtils: CleanWarUtils = get<CleanWarUtils>()
    override fun description(): String {
        return "包含血量加权,写死在代码里"
    }
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val warCardGap = number
        val damage = CleanWar.ALL_CLEAN
        if (cleanWarUtils.rivalNumLessGap(warCardGap, damage)) return unConditionWeight
        if (cleanWarUtils.lessGap(warCardGap, damage)) return unConditionWeight
        return groupWeight

    }
}