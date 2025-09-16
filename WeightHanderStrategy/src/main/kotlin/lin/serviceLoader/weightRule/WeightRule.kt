package lin.serviceLoader.weightRule

import lin.bean.ComboCard


import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.myLog

interface WeightRule {
    //

    fun id(): String

    /**
     * 统一权重状态管理入口,不建议重写
     * todo-future 这里直接操作感觉不太好,如果出现要缓存的计算的权重将不好处理
     * 根据战场
     * @param callCard 需要处理的的卡牌,todo-future 要不要去掉 这里传入是为了处理完权重信息一起处理combo组情景,
     * @param warInfo 战场信息
     */
    fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo) {

    }

    fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double
}


/**
 * 单卡权重规则
 */
interface CardRule : WeightRule {
    override fun id(): String = cardId()
    fun cardId(): String

}










