package lin.weightHandler

import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.domain.MyWarManage

interface WeightHandler : Priority {
    /**
     * todo-future 这里权重信息各自处理,要不要回收权重操作,比较好集中起来统一处理
     */
    fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage)


}

interface Priority {
    /**
     * 越大越后面执行
     */
    fun priority() = 100
}

/**
 * todo-future 看有没有必要,还没有考虑实现方案 之后权重处理器
 * 1.融合在ConditionWeightHandler能快速发现,语义和扩展会有问题
 */
interface AfterWeightHandler : Priority {
    fun afterCardWeightProcess(callCard: ComboCard, warManage: MyWarManage)
}
interface InitHandler{
    /**
     * @return 初始化结果, false将不加载Handler
     */
    fun init(infos:List<CardWeightInfo>)
}

interface DiscoverWeightHandler : Priority {
    fun cardWeight(comboCard: ComboCard):Double
}


