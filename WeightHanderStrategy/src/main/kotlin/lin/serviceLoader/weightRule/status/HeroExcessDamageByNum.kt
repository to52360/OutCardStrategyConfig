package lin.serviceLoader.weightRule.status

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.hero
import org.koin.core.component.KoinComponent

/**
 * 溢出伤害,处理
 */
class HeroExcessDamageByNum : AbsWeightCondition(), KoinComponent {

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val blood = warInfo.hero()?.blood() ?: 0
        val warStatus = warInfo.warStatus
        val excessDamage = warStatus.excessDamage
        if (excessDamage - warStatus.meSumAtc > blood) return groupWeight * 2
        val lessBlood = blood - warStatus.excessDamage * 2
        return if (lessBlood < number) groupWeight
        else unConditionWeight


    }
}
