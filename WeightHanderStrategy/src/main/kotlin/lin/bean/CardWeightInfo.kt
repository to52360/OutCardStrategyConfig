package lin.bean



import lin.myLog
import lin.serviceLoader.cardRule.WeightRule
import lin.weightHandler.condition.context.ConditionException



/**
 * 转化位置
 * [lin.serviceLoader.cardInfoProvide.DefCardWeightInfoProvide]
 * @param groupId 使用weight的值 [club.xiaojiawei.bean.CardWeight.weight]
 * @param powerWeight 检测优先级
 * 打出优先级
 * [ComboOrder.outCardPriority]
 */
data class CardWeightInfo(
    val cardId: String,
    val powerWeight: Double,
    val groupId: Double = 0.0
) {
    var useStrategy: UseStrategy = DefUseStrategy
        set(value) {
            field = if (field == DefUseStrategy) {
                value
            } else {
                throw ConditionException("暂时无法重复设置")
            }
        }

    var weightRules : List<WeightRule> = emptyList()
        private set
    fun addWeightRule(weightRule: WeightRule){
        weightRules = if(weightRules.isEmpty()){
            listOf(weightRule)
        }else{
            weightRules+weightRule
        }
    }
    fun clearWeightRule(){
        weightRules = emptyList()
    }
    var combo : Combo? = null
        private set
    fun addCombo(combo: Combo){
        this.combo?.let {
            myLog.warn { "combo组,重复设置重复设置可能有问题" }
        }
        this.combo = combo
    }

}


sealed class UseStrategy(val useType: UseType)
data object DefUseStrategy : UseStrategy(UseType.DEF)
//
data object ChangeStrategy : UseStrategy(UseType.BEFORE)
class AddCostStrategy(val cost: Int) : UseStrategy(UseType.BEFORE)


//todo-future 预留没有实现
open class Combo(val comboId: Int,val comboRule: ComboRule?,val priority:Boolean)

object DefCombo: Combo(0,null,false)




sealed class CardContext
data object DefCardContext : CardContext()
class AnyContext : CardContext() {
    //元数据 用来存储
    private val metadata: MutableMap<MetadataKey<*>, Any> = mutableMapOf()
    fun <T> putMetadata(key: MetadataKey<T>, value: T) {
        metadata[key] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getMetadata(key: MetadataKey<T>): T? = metadata[key] as T?
}

@JvmInline
value class MetadataKey<T>(val name: String)

enum class UseType {
    BEFORE,
    DEF,
    AFTER
}


