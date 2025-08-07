package lin.lifecycle

import lin.myLog

class LifecycleRegisterImpl : LifecycleRegister {
    // 存储 RuleLifecycle 类型的生命周期对象
    private val ruleLifecycles = mutableListOf<RuleLifecycle>();

    // 存储 RuleGameLifecycle 类型的生命周期对象
    private val ruleGameLifecycles = mutableListOf<RuleGameLifecycle>();

    // 注册 RuleLifecycle 实例
    fun register(lifecycle: RuleLifecycle) {
        ruleLifecycles.add(lifecycle)
    }

    // 新增：接受 Any 类型参数的 register 方法
    override fun register(any: Any) {
        if (any is RuleLifecycle) {
            register(any)
        }
        if (any is RuleGameLifecycle) {
            register(any)
        }
    }

    override fun logout(anys: List<Any>) {
        anys.forEach { any ->
            if (any is RuleLifecycle) {
                ruleLifecycles.remove(any)
            }
            if (any is RuleGameLifecycle) {
                ruleGameLifecycles.remove(any)
            }
        }

    }

    // 注册 RuleGameLifecycle 实例
    fun register(lifecycle: RuleGameLifecycle) {
        ruleGameLifecycles.add(lifecycle)
    }

    // 触发所有 RuleLifecycle 的 start 方法
    fun startAllRuleLifecycles() {
        ruleLifecycles.forEach { it.start() }
    }


    // 触发所有 RuleGameLifecycle 的 start 方法
    fun startAllGameLifecycles() {
        ruleGameLifecycles.forEach { it.start() }
    }

}