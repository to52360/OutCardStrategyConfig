package lin.bean


import lin.domain.MyWarInfo
import lin.weightHandler.condition.context.ConditionException
import lin.weightHandler.condition.define.WeightRule


/**
 * 转化位置
 * [lin.weightHandler.provide.DefCardWeightInfoProvide]
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
            if (field == DefUseStrategy) {
                field = value
            } else if (value == DefUseStrategy) {
                field = DefUseStrategy
            } else {
                throw ConditionException("暂时无法重复设置")
            }
        }

    //todo 改成这样,为了实现val功能,但是不实用,看一下是否需要该 ,增加额外复杂性
    var weightCalculate: WeightCalculate = DefWeightCalculate
        set(value) {
            if (value == DefWeightCalculate) {
                field = value
            } else
                when (val context = field) {
                    DefWeightCalculate -> field = value
                    is OutCondition -> { //转化为集合
                        if (value is OutCondition) {
                            val outCondition = OutConditions(context.weightRule)
                            outCondition.add(value.weightRule)
                            field = outCondition
                        } else {
                            throw RuntimeException("设置类型错误")
                        }

                    }

                    is OutConditions -> {
                        if (value is OutCondition) {
                            context.add(value.weightRule)
                        } else {
                            throw RuntimeException("设置类型错误")
                        }
                    }
                }
        }
}


sealed class UseStrategy(val useType: UseType)
data object DefUseStrategy : UseStrategy(UseType.DEF)
//
data object ChangeStrategy : UseStrategy(UseType.BEFORE)
class AddCostStrategy(val cost: Int) : UseStrategy(UseType.BEFORE)


//todo-future 预留没有实现
sealed class Combo
data object DefCombo : Combo()

/**
 * @param outCardPriority 打出优先级
 */
data class ComboOrder(var comboId: Int, var outCardPriority: Int) : Combo()


sealed class WeightCalculate
data object DefWeightCalculate : WeightCalculate()
class OutCondition(val weightRule: WeightRule) : WeightCalculate() {
    fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo) {
        weightRule.calculateSetWeight(callCard, myWarInfo)
    }
}

class OutConditions(weightRule: WeightRule) : WeightCalculate() {
    private val weightConditions = mutableListOf(weightRule)
    fun add(weightRule: WeightRule) = weightConditions.add(weightRule)
    fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo) {
        weightConditions.forEach {
            it.calculateSetWeight(callCard, myWarInfo)
        }
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


