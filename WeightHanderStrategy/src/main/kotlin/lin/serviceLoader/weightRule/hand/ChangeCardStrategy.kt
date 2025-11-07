package lin.serviceLoader.weightRule.hand

import lin.bean.ChangeCard
import lin.bean.ChangeGroupId
import lin.bean.ComboCard
import lin.config.CardConfig
import lin.config.UseConfig
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.serviceLoader.weightRule.ExtConfig
import lin.serviceLoader.weightRule.onWar.rival.softClean.IsAdvByMeAtc
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.my.base.deckArea
import lin.warExt.my.base.getHandCards

/**
 * 更改手牌
 * 使用继承,不知道为啥spi无法识别委托
 */
open class ChangeCardStrategy : AbsWeightCondition(), ExtConfig {
    override fun description(): String {
        return " 变更手牌"
    }

    private val isAdvByMeAtc = IsAdvByMeAtc()

    init {
        isAdvByMeAtc.setUnCondWeight(NotWeight)
    }


    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {

        //todo-future 存在魔数,没太大问题就这样了,(可以正则卡牌文本以实现更为复杂策略)
        if (warInfo.getHandCards().size > 8) return UnUseWeight
        //牌库没牌了
        if (warInfo.deckArea().size < 3) return UnUseWeight
        val handSize = warInfo.getHandCards().size

        return if (handSize < number) {
            groupWeight
        } else {
            unConditionWeight
        }


    }

    override fun cardConfigs(): List<CardConfig> {
        return listOf(UseConfig(ChangeGroupId), ChangeCard)
    }


}
