package lin.bean


import lin.lifecycle.LifecycleRegister
import lin.serviceLoader.weightRule.WeightRule
import lin.weightHandler.condition.context.NotWeight
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


/**
 * 转化位置
 * [lin.serviceLoader.cardInfoProvide.DefCardWeightInfoProvide]
 * @param groupId 使用weight的值 [club.xiaojiawei.bean.CardWeight.weight]
 * @param powerWeight 检测优先级
 */
data class CardWeightInfo(
    val cardId: String,
    val powerWeight: Double,
    val groupId: Double = 0.0,
    val changeWeight: Double = NotWeight
) : KoinComponent {

    private var _useAfterStrategy: MutableList<UseAfterStrategy>? = null
    val useAfterStrategy: List<UseAfterStrategy>
        get() = _useAfterStrategy ?: emptyList()
    private var _useBeforeStrategy: MutableList<UseBeforeStrategy>? = null
    val useBeforeStrategy: List<UseBeforeStrategy>
        get() = _useBeforeStrategy ?: emptyList()

    fun addUseStrategy(useStrategy: UseStrategy) {
        if (useStrategy is UseBeforeStrategy) {
            _useBeforeStrategy = _useBeforeStrategy.addSafe(useStrategy)
        }
        if (useStrategy is UseAfterStrategy) {
            _useAfterStrategy = _useAfterStrategy.addSafe(useStrategy)
        }
    }

    private var _changeComboRule: MutableList<ComboRule>? = null
    val changeComboRule: List<ComboRule>
        get() = _changeComboRule ?: emptyList()
    var findStrategy: FindStrategy? = null

    //最后使用暂时这样,没想到其他方案
    var lastUse = false

    private var _weightRules: MutableList<WeightRule>? = null
    val weightRules: List<WeightRule>
        get() = _weightRules ?: emptyList()

    fun addChangeComboRule(comboRule: ComboRule) {
        _changeComboRule = _changeComboRule.addSafe(comboRule)
    }
    /**
     * 存在重复添加的问题
     */
    fun addWeightRule(weightRule: WeightRule){
        setWeightRule(weightRule)
        val lifecycleRegister = get<LifecycleRegister>()
        //生命周期,游戏开始/回合开始结束调用对应方法,为了条件组有状态
        lifecycleRegister.register(weightRule)
    }

    fun setWeightRule(weightRule: WeightRule) {
        _weightRules = _weightRules.addSafe(weightRule)
    }
    fun clearWeightRule(){
        val lifecycleRegister = get<LifecycleRegister>()
        lifecycleRegister.logout(weightRules)
        _weightRules = null
    }


    private var _combos: MutableList<Combo>? = null

    val combos: List<Combo>
        get() = _combos ?: emptyList()

    fun addCombo(combo: Combo) {
        _combos = _combos.addSafe(combo)
    }

    // 扩展函数：安全添加元素到可空列表
    fun <T> MutableList<T>?.addSafe(item: T): MutableList<T> {
        return this?.apply { add(item) } ?: mutableListOf(item)
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




