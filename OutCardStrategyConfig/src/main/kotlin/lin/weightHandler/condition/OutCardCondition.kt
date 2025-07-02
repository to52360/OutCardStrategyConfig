package lin.weightHandler.condition

import lin.bean.ComboWeightInfo
import lin.dao.ComboCard
import lin.dao.WarInfo

import lin.weightHandler.condition.context.notConditionDefaultWeight

/**
 *出牌条件
 * todo id考虑用配置表
 */
abstract class OutCardCondition {
    abstract fun id(): Int

    //不符合条件减少权重
    protected val notConditionWeight = notConditionDefaultWeight
    abstract fun canUse(callCard: ComboCard, warInfo: WarInfo)
    fun create() {

    }


}


//扩展条件,从单卡,combo处 增加的额外条件
abstract class ExtOutCardCondition : OutCardCondition() {
    abstract fun extType(): ExtType
}

//todo 暂不考虑
enum class ExtType {
    OR, SWP //覆盖,AND
}

//注入数据参考组,标记
interface DepByWeightInfo {
    fun initByWeightInfo(comboWeightInfoList: List<ComboWeightInfo>)
}




