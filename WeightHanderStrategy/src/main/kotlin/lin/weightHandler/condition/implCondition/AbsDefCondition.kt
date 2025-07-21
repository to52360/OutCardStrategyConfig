package lin.weightHandler.condition.implCondition

import lin.bean.CardWeightInfo
import lin.weightHandler.condition.define.DepByWeightInfo

/**
 * todo-future 不知道还要不要了
 */
abstract  class DepRaceCacheFun<FUN_CACHE:Any>: DepByWeightInfo {
    protected lateinit var funCache :FUN_CACHE
    override fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>) {
        TODO("Not yet implemented")
    }
    abstract fun parseInfosToCache(cardWeightInfoList: List<CardWeightInfo>):FUN_CACHE
    abstract fun consumeCache(funCache:FUN_CACHE)
}
