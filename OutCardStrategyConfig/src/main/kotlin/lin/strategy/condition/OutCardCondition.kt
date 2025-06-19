package lin.strategy.condition

import lin.bean.v2.ComboWeightInfo

/**
 *出牌条件
 * todo id考虑用配置表
 */
sealed class OutCardCondition{
    //不符合条件减少权重
    private var decWeight : Int = 10
    abstract fun id():Int
    var notConditionWeight = defaultConditionWeight
}
//扩展条件,从单卡,combo处 增加的额外条件
abstract class ExtOutCardCondition : OutCardCondition(){
    abstract fun extType():ExtType
}
enum class ExtType{
    OR,SWP //覆盖,AND
}
//注入数据参考组,标记
interface DepByWeightCards {
    fun setWeightCardsById(comboWeightInfoList: Map<String, ComboWeightInfo> )
}

//标记同一组打出
interface Payoff{

}


