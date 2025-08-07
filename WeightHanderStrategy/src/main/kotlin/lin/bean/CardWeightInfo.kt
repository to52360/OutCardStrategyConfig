package lin.bean


import lin.lifecycle.LifecycleRegister
import lin.myLog
import lin.serviceLoader.weightRule.WeightRule
import lin.weightHandler.condition.context.ConditionException
import lin.weightHandler.condition.context.NotWeight
import lin.weightHandler.condition.context.OrderWeight
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.component.get
import kotlin.getValue


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
) : KoinComponent {
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
        val lifecycleRegister = get<LifecycleRegister>()
        weightRules = if(weightRules.isEmpty()){
            listOf(weightRule)
        }else{
            weightRules+weightRule
        }
        //生命周期,游戏开始/回合开始结束调用对应方法,为了条件组有状态
        lifecycleRegister.register(weightRule)
    }
    fun clearWeightRule(){
        val lifecycleRegister = get<LifecycleRegister>()
        lifecycleRegister.logout(weightRules)
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


/**
 *@param comboId todo-future  comboId 不知道有没有用了
 */
open class Combo(val comboId: Int, val comboRule: ComboRule, val priority: Boolean) {
    open fun comboProcess(callComboCard: ComboCard, comboCard: ComboCard): Double {
        return comboRule.let {
            val weight = it(comboCard)
            //优先级处理
            if (weight != NotWeight) {//表示是同一组
                val powerWeight = callComboCard.powerWeight
                //之前策略
                if (priority && powerWeight <= comboCard.powerWeight) {//增加权重
                    val addWeight = comboCard.powerWeight - powerWeight + OrderWeight //保证同组优先级最高
                    callComboCard.addWeight(addWeight)
                }

                //之后策略
                if (!priority && powerWeight >= comboCard.powerWeight) {//之后 如果不是最小,修正权重为最小
                    val addWeight = powerWeight - comboCard.powerWeight + OrderWeight //保证同组优先级最高
                    comboCard.addWeight(addWeight)
                }
            }
            weight
        }

    }
}

object DefCombo : Combo(0, { _ -> NotWeight }, false) {
    override fun comboProcess(callComboCard: ComboCard, comboCard: ComboCard): Double {
        return NotWeight
    }
}




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


