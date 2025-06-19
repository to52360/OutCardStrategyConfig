package lin.bean.v2


import lin.strategy.condition.ExtOutCardCondition

class ComboGroupInfo(
    val infoId:String,
    val startCost: Int?=0,//启动费用,没到启动费用,为0
    val baseWeight:Int?=0, //基础权重 todo 没考虑清楚是加权还是减权
)
/**
 * [club.xiaojiawei.bean.CardWeight.weight] 小数部分为combo组
 */
class ComboGroup(
    val groupId: Int,//对应的分组
    val infoId : Int,
    val comboRole : ComboRole,// 扮演的角色
    val extCondition: ExtOutCardCondition //扩展条件
)
enum class ComboRole {
    DEC,     // 减费 提前启动combo
    FIND,       // 检索 提供combo启动概率
    CORE,         //有了核心才可以启动  主组组件
    PAYOFF,       //依赖于核心产生收益组
}