package lin.serviceLoader.weightRule.def

import lin.bean.CardWeightInfo
import lin.serviceLoader.weightRule.DepByWeightInfos

/**
 * todo-future 不知道还要不要了
 */
abstract  class DepRaceCacheFun<FUN_CACHE:Any>: DepByWeightInfos {
    protected lateinit var funCache :FUN_CACHE
    override fun initByWeightInfos(cardWeightInfoList: List<List<CardWeightInfo>>) {
        TODO("Not yet implemented")
    }
    abstract fun parseInfosToCache(cardWeightInfoList: List<CardWeightInfo>):FUN_CACHE
    abstract fun consumeCache(funCache:FUN_CACHE)
}
