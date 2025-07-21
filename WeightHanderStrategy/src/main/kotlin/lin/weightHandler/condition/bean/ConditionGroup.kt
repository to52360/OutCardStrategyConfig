package lin.weightHandler.condition.bean

import kotlinx.serialization.Serializable

/**
 * [club.xiaojiawei.bean.CardWeight.weight]整数部分条件组
 * 配置信息
 * [lin.weightHandler.condition.OutCardCondition]
 * todo-future 组优先级没写
 */
@Serializable
data class ConditionGroup(
    val groupId: Int, //唯一标识
    val bindId: Double,
    val outCardConditionId: Int, //打出策略,依赖关联组 ,辅助类:核心卡没上手,依赖项:在手牌
    val depByWeightId: Double?=0.0,  //依赖权重数据
    val basePriority: Double? = 10.0, //基础优先度 ,不符合条件减优先级也就是减少权重
)

/**
 *
 * todo 只能要不要用组合,继承感觉不太好
 * 自定义加载配置性
 */
/* class ConditionByCustomize(
    val depId: Int,//组id
    val key : Int,
    groupId: Int,
    bindId: Double,
    outCardConditionId: Int,
    basePriority: Double,
     depByWeightId: Double
) :
    ConditionGroup(
        groupId,
        bindId,
        outCardConditionId,
        basePriority,
        depByWeightId
    )*/

