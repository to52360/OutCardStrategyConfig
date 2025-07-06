package lin.weightHandler.condition.implCondition.util.temp

import club.xiaojiawei.config.log
import lin.weightHandler.condition.context.ConditionException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 作废
 *
 * 一个委托，用于处理需要显式初始化的属性。
 * 在初始化（赋值）之前读取该属性将抛出由 `exceptionFactory` 创建的异常。
 *
 * @param T 属性的类型，可以是任何类型（包括函数类型）。
 *
 */
class UninitializedDelegate<T : Any>(
  private val  msg:String
) : ReadWriteProperty<Any?, T> {

    // 使用一个私有变量来存储真实的值。可空类型表示“尚未赋值”。
    private var value: T? = null

    /**
     * 当读取属性时（例如 `println(myProperty)`），此方法被调用。
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
       return value?:run{
            log.warn { msg }
            throw ConditionException(msg)
       }
    }

    /**
     * 当给属性赋值时（例如 `myProperty = someValue`），此方法被调用。
     */
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        // 这就是“初始化”的动作。我们将值存储起来。
        this.value = value
    }



}

private const val msg  ="条件组件没有初始化或者没有条件组信息"

fun <T : Any> uninitialized(): UninitializedDelegate<T> {
    return UninitializedDelegate(msg)
}
