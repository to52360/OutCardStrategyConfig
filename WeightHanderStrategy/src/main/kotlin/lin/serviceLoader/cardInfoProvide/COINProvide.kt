package lin.serviceLoader.cardInfoProvide


import club.xiaojiawei.hsscriptcardsdk.data.COIN_CARD_ID
import lin.bean.COINGroupId
import lin.bean.CardWeightInfo
import lin.bean.MetadataKey
import lin.bean.addSafe


/**
 * 暂时共用脚本的权重信息,暂时无时间研究ui配置,导致只能硬编码
 */
class COINProvide : CardWeightInfoProvide {
    companion object {
        val coinKey = MetadataKey<Int>("COIN")
    }
    /**
     * todo 存在魔数
     */
    override fun getInfos(): Map<String, CardWeightInfo> {
        val coin = CardWeightInfo(COIN_CARD_ID, -20.0)
        //标记快速查询
        coin.useGroupId = COINGroupId
        coin.cardContext = coin.cardContext.addSafe(coinKey, 1)
        return mapOf(coin.cardId to coin )
    }
}