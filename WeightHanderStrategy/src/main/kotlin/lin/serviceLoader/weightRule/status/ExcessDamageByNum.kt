package lin.serviceLoader.weightRule.status

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.overLimitByDamage
import lin.warExt.my.base.hero
import org.koin.core.component.KoinComponent

/**
 * 保命牌
 * 溢出伤害,处理
 */
class ExcessDamageByNum : AbsWeightCondition(), KoinComponent {
    //额外权重表示更多情况的浮动权重,因为都是伴随暂时放在一起  后续变化要拆分一下
    var extWeight = NotWeight
    override var groupWeight: Double = CostWeight
        set(value) {
            field = value
            extWeight = (value / 2).coerceAtMost(CostWeight)

        }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val blood = warInfo.hero()?.blood() ?: 0

        val warStatus = warInfo.warStatus
        val excessDamage = warStatus.excessDamage
        //溢出太严重,没那个能力
        if (warStatus.overLimitByDamage()) return groupWeight - extWeight

        //下一回合叫杀情况
        if (blood <= number || excessDamage - warStatus.meSumAtc > blood) return groupWeight + extWeight


        //下下一回合会有危险
        val lessBlood = blood - warStatus.excessDamage * 2
        return if (lessBlood < number) groupWeight
        else unConditionWeight


    }
}
