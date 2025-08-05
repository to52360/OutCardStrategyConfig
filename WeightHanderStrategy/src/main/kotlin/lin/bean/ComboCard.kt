package lin.bean

import club.xiaojiawei.bean.Card

import lin.weightHandler.condition.context.BaseWeight
import lin.weightHandler.condition.context.NotWeight


/**
 * @param card select 状态逃逸,增加复杂性和不太安全可能会改变,优点灵活
 * select 先进行可行性,再分析权责,重新设计ComboCard,例如combo组和condition是不是具有普适
 */
typealias ComboRule = (ComboCard) -> Double
  class ComboCard (private val cardWeightInfo: CardWeightInfo?=null, val card: Card){
    //select 打出刷新 针对改变手牌
    var useStrategy = cardWeightInfo?.useStrategy?: DefUseStrategy

    val weightCalculate = cardWeightInfo?.weightCalculate?: DefWeightCalculate

    //select 同组加权可以移到condition(每个只做一件事方便开发),但是要考虑继承关系,可以考虑委托方式,现在先测可行性
    //todo combo组没有实现
    var combo : Combo = DefCombo
    private var comboRule : ComboRule? =null

    //基础信息
    fun groupId() = cardWeightInfo?.groupId
    fun cardId() = card.cardId
    fun getCost()= card.cost
    //select 暂定直接修改,缺点:状态修改到处是无法追踪,要验证状态变化将很复杂,

    val basePowerWeight = cardWeightInfo?.powerWeight?:BaseWeight
    // 出牌权重
    val  powerWeight :Double
        get() = basePowerWeight+extPowerWeight
    var  extPowerWeight = NotWeight
    /**
     * 权重累加方法
     */
    fun addWeight(weight:Double){
        extPowerWeight+=weight
    }
    fun cleanWeight(){
        extPowerWeight=NotWeight
    }

    /**
     * todo-future   or条件判断,存在问题(需要严格的顺序),目前不想大改先这样
     */
    fun isBaseWeight(): Boolean {
      return  extPowerWeight==NotWeight
    }

    /**
     * 设置
     */
    fun setComboWeightAndId(comboRule:ComboRule){
        this.comboRule = comboRule
    }


    //在同一组会增加权重
    fun comboAddWeight(comboCard: ComboCard):Double{
        return comboRule?.let {
             it(comboCard)
        }?:NotWeight
    }

    /**
     * todo-future 还需引入策略(全局策略,组策略,卡策略,来解决能不能使用),什么情况卖,什么情况不卖
     * 暂时 小于0为不可使用
     */
    fun useAble():Boolean = powerWeight>=NotWeight


    /**
     * todo-future  用于处理重新生成comboCard时候判断是否重复,重新生成没有这么复杂的逻辑,但是效率有问题
     *  这样操作其他比较会不会有问题?
     */
    override fun equals(other: Any?): Boolean {
        //select 比较cardId还是entityId,没有想清楚,先
        return  other?.let {
            if(this === other)  true
            else
            when(other) {
                is ComboCard -> {
                    card.entityId == other.card.entityId
                }
                is Card ->card.entityId == other.entityId
                else ->  false
            }
        }?:false
    }

    override fun hashCode(): Int {

        return card.entityId.hashCode()*31
    }

    override fun toString(): String {
        return "ComboCard{${cardId()},${card.entityName},${card.entityId},${powerWeight}}"
    }

}

