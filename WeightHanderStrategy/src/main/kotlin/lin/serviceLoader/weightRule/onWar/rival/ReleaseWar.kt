package lin.serviceLoader.weightRule.onWar.rival

import lin.bean.CleanWarId
import lin.bean.ComboCard
import lin.domain.context.UnUseWeight


/**
 * todo 数量可以用权重解决
 */
abstract class ReleaseWar(var warCardGap: Int) : CleanWar(CleanWarId) {

    override fun name(): String {
        return "解场用的之aoe"
    }

    override fun calWeight(callCard: ComboCard): Double {
        if (cleanWarUtils.rivalNumLessGap(warCardGap)) {
            return UnUseWeight
        }
        val damage = cache.getDamageById(callCard)
        cleanWarUtils.cleanOnce(damage)

        if (cleanWarUtils.lessGap(warCardGap, damage)) return UnUseWeight
        if (damage == ALL_CLEAN) return groupWeight
        val cutWeight = unConditionWeight * cleanWarUtils.unPassRate(damage)
        return groupWeight + cutWeight
    }


}

class DepNumRelWarByAll : ReleaseWar(0) {

    override fun setNum(num: Int) {
        this.warCardGap = num

    }
}



