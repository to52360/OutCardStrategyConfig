package lin.weightHandler

import club.xiaojiawei.bean.Card
import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.dao.MyWarManage


/**
 * select 服务发现为了方便没有采用更为安全的工厂模式
 */
interface HandlerFactory<T>{
    fun create(t:T): WeightHandler
}
interface WeightHandler{
    /**
     * todo-future 这里权重信息各自处理,要不要回收权重操作,比较好集中起来统一处理
     */
    fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage)

    /**
     * 手牌变更处理
     */
    fun handChaWeightProcess(callCard: ComboCard, warManage: MyWarManage){

    }
    fun priority() = 100
    fun gameStart(){

    }
    fun gameEnd(){

    }
}
interface InitHandler{
    /**
     * @return 初始化结果, false将不加载Handler
     */
    fun init(infos:List<CardWeightInfo>)
}

interface CardWeightHandler{
    fun cardWeight(card: Card):Double
}


