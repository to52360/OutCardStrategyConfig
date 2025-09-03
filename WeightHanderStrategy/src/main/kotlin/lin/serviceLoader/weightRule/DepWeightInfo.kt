package lin.serviceLoader.weightRule

import lin.bean.CardWeightInfo
import lin.myLog
import lin.weightHandler.condition.bean.ConditionGroup

/**
 * 注入依赖数据参考组,标记接口
 * 注入分组需要依赖权重表的信息具体数据
 * 从表weight_group配置
 */
interface DepWeightInfo : DepProcessor {
    override fun process(conditionGroup: ConditionGroup, weightGroupInfos: Map<Double, List<CardWeightInfo>>) {
        val depWeightInfos = mutableListOf<CardWeightInfo>()
        conditionGroup.depByWeightIds.forEach { depId ->
            weightGroupInfos[depId]?.run {
                depWeightInfos.addAll(this)
            }
        }
        initByWeightInfo(depWeightInfos)
    }
    /**
     * select  自定义初始化方法,有没有采用工厂模式
     * 条件(condition)依赖权重组信息
     *
     */
    fun initByWeightInfo(cardWeightInfoList: List<CardWeightInfo>)

}

/**
 * 依赖分组id
 * 从表weight_group配置
 */
interface DepByWeightGroupId : DepProcessor {
    override fun process(conditionGroup: ConditionGroup, weightGroupInfos: Map<Double, List<CardWeightInfo>>) {
        initByGroupIds(conditionGroup.depByWeightIds)
    }
    /**
     *
     * 依赖分组
     */
    fun initByGroupIds(groupIds: Array<Double>)

}


interface DepProcessor {
    /**
     * todo-future 这里使用ConditionGroup会引入无关状态
     */
    fun processAndVerify(conditionGroup: ConditionGroup, weightGroupInfos: Map<Double, List<CardWeightInfo>>): Boolean {
        if (conditionGroup.depByWeightIds.isEmpty()) {
            myLog.warn { "在权重表找不到对应分组信息${conditionGroup.depByWeightIds}" }
            return false
        }
        val errorId = StringBuffer()
        conditionGroup.depByWeightIds.forEach { depId ->
            weightGroupInfos[depId] ?: run {
                errorId.append(depId)
                errorId.append("\n")
            }
        }
        if (errorId.isNotEmpty()) {
            myLog.warn { "条件组需要绑定的数据没有在权重表找到$errorId" }
            return false
        }


        //处理
        process(conditionGroup, weightGroupInfos)
        return true

    }

    fun process(conditionGroup: ConditionGroup, weightGroupInfos: Map<Double, List<CardWeightInfo>>)
}