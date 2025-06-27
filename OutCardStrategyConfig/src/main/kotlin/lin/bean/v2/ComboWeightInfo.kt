package lin.bean.v2




/**
 * [club.xiaojiawei.bean.CardWeight]
 * todo 暂定 不知道是否需要考虑
 */
class ComboWeightInfo(//存储在map,cardId省略
    val groupId: Double,      // 分组id CardWeight的Weight整数部分为分组id 例如 16.1 16为分组 1为关联组
    val conditionGroup: ConditionGroup,
    val powerWeight: Double,//出牌权重 为CardWeight的powerWeight整数部分
)