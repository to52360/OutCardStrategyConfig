package lin.bean.v2

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.CardWeight
import lin.strategy.ComboRole

/**
 * [CardWeight]
 */
class ComboWeightInfo(//存储在map,cardId省略
    val groupId: Int,      // 分组id CardWeight的Weight整数部分为分组id 例如 16.1 16为分组 1为关联组
    val powerWeight: Int,//出牌权重 为CardWeight的powerWeight整数部分
    val comboRole: ComboRole //combo组扮演的角色 暂定不知道要不要冗余
)