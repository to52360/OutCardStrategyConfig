package lin.dao.v1

import club.xiaojiawei.bean.Card
import lin.bean.v2.ComboWeightInfo


/**
 * @param card todo 状态逃逸,增加复杂性和不太安全可能会改变,优点灵活
 */
class ComboCard (private val comboWeightInfo: ComboWeightInfo?=null, val card: Card){
    fun groupId() = comboWeightInfo?.groupId
    fun cardId() = card.cardId
    //出牌权重
    var  varPowerWeight :Double = comboWeightInfo?.powerWeight?:0.0
        set(value) {
            field += value
        }
    private var comboWeight : Double =0.0
    private var cardId:String? = null

    fun setComboWeightAndId(cardId:String,comboWeight: Double){
        this.cardId = cardId
        this.comboWeight = comboWeight
    }
    //在同一组会增加权重
    fun comboAddWeight(comboCards: List<Card>):Double{
        return  cardId?.let {
           if(comboCards.first().cardId==it)
               comboWeight
            else
                0.0
        }?:0.0
    }



    fun getCost()= card.cost

}
