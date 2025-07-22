package lin.weightHandler.condition.define

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.dao.MyWarInfo
interface WeightRule{
    //
    /**
     * select 符合条件增加权重,先试一下
     * todo-future 这里直接操作感觉不太好,如果出现要缓存的计算的权重将不好处理
     * 根据战场
     * @param callCard 需要处理的的卡牌,todo 要不要去掉 这里传入是为了处理完权重信息一起处理combo组情景,
     * @param myWarInfo 战场信息
     */
    fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo)
}

/**
 * 加权条件
 * todo-future id考虑用配置表
 */
interface WeightCondition:WeightRule {
     //唯一
     fun id(): Int
     fun name(): String {
         return this.javaClass.simpleName
     }
}

/**
 * 单卡权重规则
 */
interface CardRule: WeightRule {
    fun cardId(): String
}




//注入数据参考组,标记
interface DepByWeightInfo {
    /**
     * select  自定义初始化方法,有没有采用工厂模式
     * 条件(condition)依赖权重组信息
     *
     */
     fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>)

}





