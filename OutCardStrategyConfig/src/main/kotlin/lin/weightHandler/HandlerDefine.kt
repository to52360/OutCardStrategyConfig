package lin.weightHandler

import club.xiaojiawei.bean.Card
import lin.bean.ComboWeightInfo
import lin.dao.ComboCard
import lin.dao.WarInfo


interface HandlerFactory<T>{
    fun create(t:T): WeightHandler
}
interface WeightHandler{
    fun cardWeightProcess(callCard: ComboCard, warInfo: WarInfo)
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


