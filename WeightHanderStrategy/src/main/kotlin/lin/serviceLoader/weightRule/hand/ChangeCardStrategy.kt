package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeGroupId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.onWar.rival.CleanWar.Companion.ALL_CLEAN
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.CleanWarUtils
import lin.warExt.my.base.getHandCards
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 更改手牌
 */
class ChangeCardStrategy : AbsWeightCondition(), KoinComponent {
    val cleanWarUtils = get<CleanWarUtils>()
    override fun description(): String {
        return "groupWeight作为真正的权重,unConditionWeight和powerWeight用来判断数量"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val handSize = warInfo.getHandCards().size
        //临时性
        cleanWarUtils.reLoadOnce()
        if (!cleanWarUtils.lessGap(3, ALL_CLEAN)) return unConditionWeight

        if (handSize < number) {
            callCard.useGroupId = ChangeGroupId
            return groupWeight
        } else {
            if (warInfo.getHandCards().size > 8) {
                //todo实验性
                /*                if (warInfo.getCost() - callCard.cost() > 5 && !warInfo.playCardIsFull()) { //可以打出
                                    callCard.useGroupId = LastUseGroupId
                                    return unConditionWeight
                                }*/
                return UnUseWeight
            }
            callCard.useGroupId = ChangeGroupId
            return unConditionWeight
        }


    }


}
