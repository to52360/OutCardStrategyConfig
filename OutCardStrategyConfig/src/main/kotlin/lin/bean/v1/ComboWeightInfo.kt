package lin.bean.v1

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.CardWeight

/**
 * [CardWeight]
 */
class ComboWeightInfo(
    val cardId: String,
    val groupId: Double,      // 组合分组ID, e.g., 16.1
    val strategyId: Int,      // 策略分组ID, e.g., 16
    val powerWeight: Double,
    val isCore: Boolean       // 是否是其 groupId 内的核心卡

)