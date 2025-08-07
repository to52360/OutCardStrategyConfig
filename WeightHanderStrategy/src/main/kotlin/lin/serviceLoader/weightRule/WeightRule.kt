package lin.serviceLoader.weightRule

import lin.bean.ComboCard


import lin.domain.WarInfo

interface WeightRule {
    //
    /**
     * select 符合条件增加权重,先试一下
     * todo-future 这里直接操作感觉不太好,如果出现要缓存的计算的权重将不好处理
     * 根据战场
     * @param callCard 需要处理的的卡牌,todo 要不要去掉 这里传入是为了处理完权重信息一起处理combo组情景,
     * @param warInfo 战场信息
     */
    fun calculateSetWeight(callCard: ComboCard, warInfo: WarInfo)
}


/**
 * 单卡权重规则
 */
interface CardRule : WeightRule {
    fun cardId(): String
}










