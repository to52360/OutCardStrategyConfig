package lin.bean.v2

import lin.weightHandler.condition.OutCardCondition

/**
 * [club.xiaojiawei.bean.CardWeight.weight]整数部分条件组
 * 配置信息
 * [lin.strategy.condition.OutCardCondition]
 */
class ConditionGroup(
    val groupId:Int, //唯一标识
    val outCardCondition : OutCardCondition, //打出策略,依赖关联组 ,辅助类:核心卡没上手,依赖项:在手牌
    val conditionType:ConditionType,
    val referenceIds : Array<Double>, //与CardWeight.weight关联,打出条件参考信息
    val basePriority: Int?=10 //基础优先度 ,不符合条件减优先级也就是减少权重
    )

/**
 * 配置文件
 */
class ConditionConfig(
    val groupId:Int, //唯一标识
    val outCardConditionId : Int, //打出策略,依赖关联组 ,辅助类:核心卡没上手,依赖项:在手牌
    val referenceIds : Array<Double>, //与CardWeight.weight关联,打出条件参考信息
    val basePriority: Int?=10 //基础优先度 ,不符合条件减优先级也就是减少权重
)
enum class ConditionType {
    DEFAULT,             //默认条件
    CHANGE,               //会变化
    COMBO,         //需要一起打出
    OTHER          //用于标记没有
}