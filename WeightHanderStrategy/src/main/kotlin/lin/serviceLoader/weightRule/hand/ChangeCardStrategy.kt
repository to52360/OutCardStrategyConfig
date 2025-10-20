package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeGroupId
import lin.bean.ComboCard
import lin.config.CardConfig
import lin.config.UseConfig
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.ExtConfig
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.WarStatus
import lin.warExt.my.base.getHandCards
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 更改手牌
 */
class ChangeCardStrategy : AbsWeightCondition(), KoinComponent, ExtConfig {
    val warStatus = get<WarStatus>()
    override fun description(): String {
        return "groupWeight作为真正的权重,unConditionWeight和powerWeight用来判断数量"
    }


    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        //todo-future 存在魔数,没太大问题就这样了,(可以正则文本和复杂策略)
        if (warInfo.getHandCards().size > 8) return UnUseWeight
        val handSize = warInfo.getHandCards().size
        //todo 临时性,没优势重新加载
        warStatus.reloadByOption()
        if (!warStatus.isAdv) return NotWeight

        return if (handSize < number) {
            groupWeight
        } else {
            unConditionWeight
        }


    }

    override fun cardConfigs(): List<CardConfig> {
        return listOf(UseConfig(ChangeGroupId))
    }


}
