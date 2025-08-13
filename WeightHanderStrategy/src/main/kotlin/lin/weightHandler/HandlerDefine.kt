package lin.weightHandler

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.domain.MyWarManage


interface WeightHandler{
    /**
     * todo-future 这里权重信息各自处理,要不要回收权重操作,比较好集中起来统一处理
     */
    fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage)


    /**
     * 越大越后面执行
     */
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

interface DiscoverWeightHandler {
    fun cardWeight(comboCard: ComboCard):Double
}


