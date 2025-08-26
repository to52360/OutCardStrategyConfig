package lin.serviceLoader.weightRule.hand

import lin.bean.ComboCard
import lin.serviceLoader.weightRule.utils.DepByWeightGroupIdDelegate
import lin.serviceLoader.weightRule.utils.DepToPredicate
import lin.serviceLoader.weightRule.utils.PredicateByGroupId

/**
 * 用于处理丢弃的概率
 * 存在问题 使用之后权重会变
 */
class HandNumWeightId : AbstractHandArea(), DepByWeightGroupIdDelegate<DepToPredicate> by PredicateByGroupId() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
         var num = 0
         handCards.forEach {
             if(depToPredicate(it))
                 num++
         }
        if (num == 0) return -groupWeight
        val prob = num.toFloat() / handCards.size
        if (prob < 0.4F) return -groupWeight * prob
        return prob * groupWeight
    }

    override fun description(): String {
        return "满足条件卡牌数量"
    }


}