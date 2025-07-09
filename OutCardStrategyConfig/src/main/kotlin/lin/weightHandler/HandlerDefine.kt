package lin.weightHandler

import club.xiaojiawei.bean.Card
import lin.weightHandler.condition.bean.ComboWeightInfo
import lin.dao.ComboCard
import lin.dao.MyWarManage


/**
 * select 服务发现为了方便没有采用更为安全的工厂模式
 */
interface HandlerFactory<T>{
    fun create(t:T): WeightHandler
}
interface WeightHandler{
    fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage)
    fun priority() = 0
    fun gameStart(){

    }
    fun gameEnd(){

    }
}
interface InitHandler{
    fun init(infos:List<ComboWeightInfo>)
}

interface CardWeightHandler{
    fun cardWeight(card: Card):Double
}


