package lin.bean.v2

import lin.weightHandler.condition.ExtOutCardCondition

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
    DEC,             //减费
    FIND,               //检索发现,当核心不存在
    GEN_PAYOFF,         //参生潜在收益组
    PAYOFF,       //依赖于核心产生收益组
}
interface ComboCondition {

}

