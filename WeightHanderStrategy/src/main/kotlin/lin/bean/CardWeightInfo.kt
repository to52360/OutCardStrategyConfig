package lin.bean


import lin.domain.combo.FindStrategy
import lin.domain.combo.UseAfterStrategy
import lin.domain.combo.UseBeforeStrategy
import lin.domain.combo.UseStrategy
import lin.domain.context.NotWeight
import lin.lifecycle.LifecycleRegister
import lin.serviceLoader.weightRule.WeightRule
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


/**
 * 转化位置
 * [lin.serviceLoader.cardInfoProvide.DefCardWeightInfoProvide]
 * @param groupId 使用weight的值 [club.xiaojiawei.hsscriptcardsdk.bean.CardWeight.weight]
 * @param powerWeight 检测优先级
 *
 * todo-future (三合一了)信息太多可以拆分.集合类的变量应该添加处理上下文(操作日志和处理器之间的通信)
 */
data class CardWeightInfo(
    val cardId: String,
    val powerWeight: Double,
    val groupId: Double = 0.0,
    val changeWeight: Double = NotWeight
) : KoinComponent {

    //使用相关
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

    //查找策略
    var findStrategy: FindStrategy? = null



    private var _weightRules: MutableList<WeightRule>? = null
    val weightRules: List<WeightRule>
        get() = _weightRules ?: emptyList()


    /**
     * 添加权重规则
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


    //combo相关
    //最后使用暂时这样,没想到其他方案
    var lastUse: LastUse? = null
    private var _combos: MutableList<Combo>? = null

    val combos: List<Combo>?
        get() = _combos

    fun addCombo(combo: Combo) {
        _combos = _combos.addSafe(combo)
    }

    // 扩展函数：安全添加元素到可空列表
    fun <T> MutableList<T>?.addSafe(item: T): MutableList<T> {
        return this?.apply { add(item) } ?: mutableListOf(item)
    }

    //换牌相关

    private var _changeComboRule: MutableList<ComboRule>? = null
    val changeComboRule: List<ComboRule>
        get() = _changeComboRule ?: emptyList()

    fun addChangeComboRule(comboRule: ComboRule) {
        _changeComboRule = _changeComboRule.addSafe(comboRule)
    }

    //战场相关
    var toDie = false

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




