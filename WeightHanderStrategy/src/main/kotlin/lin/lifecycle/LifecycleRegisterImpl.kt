package lin.lifecycle

class LifecycleRegisterImpl : LifecycleRegister {
    // 存储 RoundLifecycle 类型的生命周期对象
    private val roundLifecycles = mutableListOf<RoundLifecycle>();

    // 存储 GameLifecycle 类型的生命周期对象
    private val gameLifecycles = mutableListOf<GameLifecycle>();

    // 注册 RoundLifecycle 实例
    fun register(lifecycle: RoundLifecycle) {
        roundLifecycles.add(lifecycle)
    }

    // 新增：接受 Any 类型参数的 register 方法
    override fun register(any: Any) {
        if (any is RoundLifecycle) {
            register(any)
        }
        if (any is GameLifecycle) {
            register(any)
        }
    }

    override fun logout(anys: List<Any>) {
        anys.forEach { any ->
            if (any is RoundLifecycle) {
                roundLifecycles.remove(any)
            }
            if (any is GameLifecycle) {
                gameLifecycles.remove(any)
            }
        }

    }

    // 注册 GameLifecycle 实例
    fun register(lifecycle: GameLifecycle) {
        gameLifecycles.add(lifecycle)
    }

    // 触发所有 RoundLifecycle 的 start 方法
    fun startAllRuleLifecycles() {
        roundLifecycles.forEach { it.start() }
    }


    // 触发所有 GameLifecycle 的 start 方法
    fun startAllGameLifecycles() {
        gameLifecycles.forEach { it.start() }
    }

}