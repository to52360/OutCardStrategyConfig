package lin.weightHandler.condition.define

import club.xiaojiawei.config.log
import lin.weightHandler.condition.bean.ComboWeightInfo
import lin.dao.ComboCard
import lin.dao.WarInfo
import lin.weightHandler.condition.context.ConditionException

/**
 *出牌条件
 * todo id考虑用配置表
 */
interface OutCardCondition {
     fun id(): Int

    //不符合条件减少权重
     fun onWarInfoProcessWeight(callCard: ComboCard, warInfo: WarInfo)
    fun create() {

    }


}


//扩展条件,从单卡,combo处 增加的额外条件
 interface ExtOutCardCondition : OutCardCondition {
     fun extType(): ExtType
}

//todo 暂不考虑
enum class ExtType {
    OR, SWP //覆盖,AND
}

//注入数据参考组,标记
interface DepByWeightInfo {
    /**
     * select  自定义初始化方法,有没有采用工厂模式
     */
     fun initByWeightInfo(comboWeightInfoList: List<ComboWeightInfo>)

}

abstract  class DepRaceCacheFun<FUN_CACHE:Any>: DepByWeightInfo {
    protected lateinit var funCache :FUN_CACHE
    override fun initByWeightInfo(comboWeightInfoList: List<ComboWeightInfo>) {
        TODO("Not yet implemented")
    }
}




