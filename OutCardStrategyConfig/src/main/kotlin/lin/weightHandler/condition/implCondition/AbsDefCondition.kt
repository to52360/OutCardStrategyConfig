package lin.weightHandler.condition.implCondition

import lin.weightHandler.condition.bean.ComboWeightInfo
import lin.weightHandler.condition.define.DepByWeightInfo

abstract  class DepRaceCacheFun<FUN_CACHE:Any>: DepByWeightInfo {
    protected lateinit var funCache :FUN_CACHE
    override fun initByWeightInfo(comboWeightInfoList: List<ComboWeightInfo>) {
        TODO("Not yet implemented")
    }
    abstract fun parseInfosToCache(comboWeightInfoList: List<ComboWeightInfo>):FUN_CACHE
    abstract fun consumeCache(funCache:FUN_CACHE)
}
