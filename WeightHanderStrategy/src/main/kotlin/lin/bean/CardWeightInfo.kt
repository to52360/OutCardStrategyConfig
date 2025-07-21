package lin.bean

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import lin.dao.MyWarInfo
import lin.weightHandler.condition.context.ConditionException
import lin.weightHandler.condition.define.WeightCondition


/**
 * 转化位置
 * [lin.weightHandler.provide.DefCardWeightInfoProvide]
 * @param groupId 使用weight的值 [club.xiaojiawei.bean.CardWeight.weight]
 * @param powerWeight 检测优先级
 * 打出优先级
 * [ComboOrder.outCardPriority]
 */
@Serializable
data class CardWeightInfo(
    val cardId: String,
    val powerWeight: Double,
    val groupId: Double = 0.0
){
    @Transient
    @Contextual
    var useStrategy: UseStrategy = DefUseStrategy
    set(value){
        if(field== DefUseStrategy){
            field = value
        }else{
            throw ConditionException("暂时无法重复设置")
        }
    }
    @Transient
    @Contextual
    var weightCalculate: WeightCalculate = DefWeightCalculate
        set(value){
            when(val context = field){
                DefWeightCalculate -> field = value
                is OutCondition -> { //转化为集合
                    if(value is OutCondition){
                        val outCondition = OutConditions(context.weightCondition)
                        outCondition.add(value.weightCondition)
                        field = outCondition
                    }else{
                        throw RuntimeException("设置类型错误")
                    }

                }
                is OutConditions -> {
                    if(value is OutCondition){
                        context.add(value.weightCondition)
                    }else{
                        throw RuntimeException("设置类型错误")
                    }
                }
            }
        }
}



sealed class UseStrategy(val useType : UseType)
data object DefUseStrategy : UseStrategy(UseType.DEF)
data object ChangeStrategy : UseStrategy(UseType.BEFORE)
class AddCostStrategy(val cost: Int) : UseStrategy(UseType.BEFORE)


//todo-future 预留没有实现
sealed class Combo
data object DefCombo : Combo()

/**
 * @param outCardPriority 打出优先级
 */
data class ComboOrder(var comboId :Int,var outCardPriority : Int):Combo()




sealed class WeightCalculate
data object DefWeightCalculate : WeightCalculate()
class OutCondition( val weightCondition: WeightCondition): WeightCalculate(){
    fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo){
        weightCondition.calculateSetWeight(callCard, myWarInfo)
    }
}
class OutConditions( weightCondition: WeightCondition): WeightCalculate(){
    private val weightConditions = mutableListOf(weightCondition)
    fun add(weightCondition: WeightCondition)  = weightConditions.add(weightCondition)
    fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo){
        weightConditions.forEach{
            it.calculateSetWeight(callCard, myWarInfo)
        }
    }
}
sealed class CardContext
data object DefCardContext : CardContext()
class AnyContext: CardContext(){
    //元数据 用来存储
    private val metadata: MutableMap<MetadataKey<*>, Any>  = mutableMapOf()
    fun <T> putMetadata(key: MetadataKey<T>, value: T) {
        metadata[key] = value as Any
    }
    @Suppress("UNCHECKED_CAST")
    fun <T> getMetadata(key: MetadataKey<T>): T? = metadata[key] as T?
}
@JvmInline
value class MetadataKey<T>(val name: String)

enum class UseType{
    BEFORE,
    DEF,
    AFTER
}

enum class CardType {
    DEFAULT,             //默认值无意义
    CHANGE,               //改变手牌
    ADD_COST,         //增加费用的
}

val AddCost by lazy { MetadataKey<Int>("AddCost") }
