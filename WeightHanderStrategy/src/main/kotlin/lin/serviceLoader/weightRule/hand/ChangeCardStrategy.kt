package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeGroupId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.WarStatus
import lin.warExt.my.base.getHandCards
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 更改手牌
 */
class ChangeCardStrategy : AbsWeightCondition(), KoinComponent {
    val warWeight = get<WarStatus>()
    override fun description(): String {
        return "groupWeight作为真正的权重,unConditionWeight和powerWeight用来判断数量"
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val handSize = warInfo.getHandCards().size
        //todo 临时性
        warWeight.reLoadOnce()
        callCard.useGroupId = ChangeGroupId
        if (!warWeight.isAdvByAvgAtc(5)) return NotWeight
        if (handSize < number) {

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
            return unConditionWeight
        }


    }


}
