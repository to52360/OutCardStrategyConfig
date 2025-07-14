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
        val 硬币 = CardWeightInfo("",5.0)
        硬币.metadata[AddCost] = 1.0
        return mapOf(硬币.cardId to 硬币 )
    }
}