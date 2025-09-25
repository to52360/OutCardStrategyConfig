package lin.serviceLoader.weightRule.onWar

import lin.bean.CleanWarId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.strategy.UseBeforeStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.lifecycle.RoundLifecycle
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.warExt.action.cleanPlay
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.base.meBlood
import lin.warExt.my.twoLambda.findAtcSum
import lin.warExt.my.twoLambda.findMeTauntSumBlood
import lin.warExt.rival.findRivalAtcSum
import lin.warExt.rival.rivalAllCardsByPlayArea

/**
 * todo 数量可以用权重解决
 */
abstract class ReleaseWar(var warCardGap: Int, var rivalAtc: Int, var lessBlood: Int) : AbsWeightCondition(),
    RoundLifecycle, UseBeforeStrategy {
    override fun name(): String {
        return "解场用的之aoe"
    }
    private var clean = true
    private var first = true
    private val useBeforeStrategy: MutableList<UseBeforeStrategy> by lazy { mutableListOf(this) }

    //todo 实现每个回合执行一次
    override fun start(warInfo: WarInfo) {
        clean = true
        first = true
    }
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {
        val weight = calWeight(warInfo)
        if (weight + callCard.powerWeight > NotWeight) {
            callCard.useBeforeStrategy?.add(this) ?: run { callCard.useBeforeStrategy = useBeforeStrategy }
            callCard.useGroupId = CleanWarId
            return weight
        }
        //todo 存在会打出情况
        return weight
    }

    override fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        warInfo.cleanPlayByRoundOnce()
        if (first) {
            first = false
            return
        } else {
            if (calWeight(warInfo) == UnUseWeight) {
                comboCard.unUse()
            }
        }
    }

    private fun calWeight(warInfo: WarInfo): Double {
        val rivalCards = warInfo.rivalAllCardsByPlayArea()
        val meCards = warInfo.getPlayCards()
        var warCardGap = rivalCards.size - meCards.size
        if (rivalCards.size >= this.warCardGap && clean) {
            warInfo.cleanPlay()
            warCardGap = rivalCards.size - meCards.size
            clean = false
        }
        if (warCardGap >= this.warCardGap) {
            return groupWeight
        }

        //场攻大于12
        val rivalAtc = warInfo.findRivalAtcSum() - warInfo.findAtcSum()
        if (rivalAtc > this.rivalAtc) return groupWeight

        //快没血
        val meBlood = warInfo.meBlood() + warInfo.findMeTauntSumBlood()
        if (meBlood - rivalAtc < lessBlood) return groupWeight
        return UnUseWeight
    }
}

class DepNumRelWar() : ReleaseWar(0, 0, 0) {

    val atcFactory = 3
    val bloodFactory = 2
    override fun setNum(num: Int) {
        this.warCardGap = num
        this.rivalAtc = num * atcFactory
        this.lessBlood = num * bloodFactory

    }
}

