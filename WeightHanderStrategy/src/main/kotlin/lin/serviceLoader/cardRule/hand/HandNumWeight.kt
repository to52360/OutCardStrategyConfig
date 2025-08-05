package lin.serviceLoader.cardRule.hand

import lin.bean.ComboCard
import lin.serviceLoader.cardRule.utils.DepByWeightGroupDelegate
import lin.serviceLoader.cardRule.utils.DepToPredicate
import lin.serviceLoader.cardRule.utils.PredicateByGroup

/**
 * 用于处理丢弃的概率
 * 存在问题 使用之后权重会变
 */
class HandNumWeight : AbstractHandArea(),DepByWeightGroupDelegate<DepToPredicate> by PredicateByGroup() {
    override fun onWarInfoProcessWeight(handCards: List<ComboCard>): Double {
         var num = 0
         handCards.forEach {
             if(depToPredicate(it))
                 num++
         }
         return (num/handCards.size)*groupWeight
    }

    override fun id() = 25073001

}