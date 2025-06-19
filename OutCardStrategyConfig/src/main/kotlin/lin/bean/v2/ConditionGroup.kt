package lin.bean.v2

/**
 * [club.xiaojiawei.bean.CardWeight.weight]整数部分条件组
 */
class ConditionGroup(
    val groupId:Int, //打出条件组
    val outCardStrategyId : String, //打出策略,依赖关联组 ,辅助类:核心卡没上手,依赖项:在手牌
    val referenceIds : Array<Double>, //与CardWeight.weight关联,打出条件参考信息
    val basePriority: Int?=10 //基础优先度 ,不符合条件减优先级也就是减少权重
    )