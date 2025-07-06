package lin.dao

import club.xiaojiawei.bean.Card
import lin.weightHandler.condition.bean.ComboWeightInfo
import lin.weightHandler.condition.bean.Metadata
import lin.weightHandler.condition.context.defaultWeight


/**
 * @param card todo 状态逃逸,增加复杂性和不太安全可能会改变,优点灵活
 * todo 先进行可行性,再分析权责,重新设计ComboCard,例如combo组和condition是不是具有普适
 */
class ComboCard (private val comboWeightInfo: ComboWeightInfo?=null, val card: Card){
    fun groupId() = comboWeightInfo?.groupId
    fun cardId() = card.cardId
    //出牌权重
    var  varPowerWeight :Double = comboWeightInfo?.powerWeight?:0.0
        set(value) {
            field += value
        }
    //todo 同组加权可以移到condition,但是要考虑继承关系,可以考虑委托方式,现在先测可行性
    private var comboOption : ((List<ComboCard>) -> Double)? =null

    /**
     * 设置
     */
    fun setComboWeightAndId(comboOption:(selectCard:List<ComboCard>) -> Double){
        this.comboOption = comboOption
    }
    fun getMetadata(): Metadata? {
      return  comboWeightInfo?.metadata
    }
    //在同一组会增加权重
    fun comboAddWeight(comboCards: List<ComboCard>):Double{
        return comboOption?.let {
            return it(comboCards)
        }?:defaultWeight
    }

    /**
     * 小于0为不可使用
     */
    fun useAble():Boolean = varPowerWeight>=defaultWeight

    fun getCost()= card.cost

}
