package lin.serviceLoader.cardInfoProvide

import club.xiaojiawei.data.COIN_CARD_ID
import lin.bean.AddCostStrategy
import lin.bean.CardWeightInfo

/**
 * 暂时共用脚本的权重信息,暂时无时间研究ui配置,导致只能硬编码
 */
class COINProvide : CardWeightInfoProvide {
    /**
     * todo 存在魔数
     */
    override fun getInfos(): Map<String, CardWeightInfo> {
        val coin = CardWeightInfo(COIN_CARD_ID, 100.0)
        coin.useStrategy = AddCostStrategy(1)
        return mapOf(coin.cardId to coin )
    }
}