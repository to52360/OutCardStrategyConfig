package lin.weightHandler.provide

import lin.weightHandler.CardWeightInfoProvide
import lin.weightHandler.condition.bean.AddCost
import lin.weightHandler.condition.bean.CardWeightInfo

/**
 * 暂时共用脚本的权重信息,暂时无时间研究ui配置,导致只能硬编码
 */
class CardHoldType : CardWeightInfoProvide {
    /**
     * todo 还需要debug获取硬币的id
     */
    override fun getInfos(): Map<String, CardWeightInfo> {
        //存在魔数
        val 硬币 = CardWeightInfo("COIN",100.0)
        硬币.putMetadata(AddCost,1)
        return mapOf(硬币.cardId to 硬币 )
    }
}