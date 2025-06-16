package lin.bean.v1

import club.xiaojiawei.bean.CardWeight

/**
 * [CardWeight]
 */
class ComboWeightInfoV1(
    val groupId: Int,      // 分组id CardWeight的Weight整数部分为分组id 例如 16.1 16为分组 1为关联组
    val relatedGroupId:Int, //关联组
    val powerWeight: Int,//出牌权重 为CardWeight的powerWeight整数部分
    val outCardStrategyId: String,//出牌策略id, powerWeight小数部分

)