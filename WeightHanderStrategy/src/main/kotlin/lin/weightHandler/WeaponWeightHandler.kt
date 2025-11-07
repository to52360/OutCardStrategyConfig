package lin.weightHandler

import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.warExt.my.base.hasWeapon

/**
 * 武器
 */
class WeaponWeightHandler : WeightHandler {
    override fun cardWeightCompute(callCard: ComboCard, warManage: MyWarManage): Double {
        if (callCard.card.cardType == CardTypeEnum.WEAPON && warManage.hasWeapon()) {
            return UnUseWeight
        }
        return NotWeight
    }
}