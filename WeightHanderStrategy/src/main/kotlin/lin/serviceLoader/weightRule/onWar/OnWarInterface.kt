package lin.serviceLoader.weightRule.onWar

import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.serviceLoader.weightRule.AddWeightByWarInfo

interface OnWarInfo : AddWeightByWarInfo {
    override fun calculateWeight(warInfo: WarInfo): Double {
        return onPlayAreaCalcWeightByMe(warInfo.playComboCards)
    }

    fun onPlayAreaCalcWeightByMe(playAreaCards: List<ComboCard>): Double
}