package lin.serviceLoader.weightRule.hand.combo

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.serviceLoader.weightRule.ExtConfig
import lin.serviceLoader.weightRule.hand.ChangeCardStrategy
import lin.serviceLoader.weightRule.onWar.rival.softClean.IsAdvByMeAtc

class ChangeCardByAdv : ChangeCardStrategy(), ExtConfig {
    override fun description(): String {
        return "组合战场判断和手牌变更,两种功能"
    }

    private val isAdvByMeAtc = IsAdvByMeAtc()

    init {
        isAdvByMeAtc.setUnCondWeight(NotWeight)
    }

    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        /* var weight = calculateWeight(callCard, warInfo)
         if(weight>0&&isAdvByMeAtc.calculateWeight(callCard,warInfo)==NotWeight){//没有优势就先不过牌
             weight = NotWeight
         }*/
        var weight = super.calculateWeight(callCard, warInfo)
        if (weight > 0 && isAdvByMeAtc.calculateWeight(callCard, warInfo) == NotWeight) {//没有优势就先不过牌
            weight = NotWeight
        }

        return weight
    }


}