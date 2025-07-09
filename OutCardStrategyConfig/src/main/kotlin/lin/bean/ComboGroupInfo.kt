package lin.bean

import lin.weightHandler.condition.define.ExtWeightCondition

class ComboGroupInfo(
    val infoId:String,
    val startCost: Int?=0,//启动费用,没到启动费用,为0
    val baseWeight:Int?=0, //基础权重
)
/**
 * [club.xiaojiawei.bean.CardWeight.weight] 小数部分为combo组
 */
class ComboGroup(
    val groupId: Int,//对应的分组
    val infoId : Int,
    val comboRole : ComboRole,// 扮演的角色
    val extCondition: ExtWeightCondition //扩展条件
)
enum class ComboRole {
    DEC,             //减费
    FIND,               //检索发现,当核心不存在
    GEN_PAYOFF,         //参生潜在收益组
    PAYOFF,       //依赖于核心产生收益组
}
interface ComboCondition {

}

