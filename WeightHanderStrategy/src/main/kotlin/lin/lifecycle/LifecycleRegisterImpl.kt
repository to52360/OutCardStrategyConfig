package lin.lifecycle

import lin.domain.WarInfo

class LifecycleRegisterImpl : LifecycleRegister {
    // 存储 RoundLifecycle 类型的生命周期对象
    private val roundLifecycles = mutableListOf<RoundLifecycle>()
    private val roundEndLifecycles = mutableListOf<RoundEnd>()

    // 存储 GameLifecycle 类型的生命周期对象
    private val gameLifecycles = mutableListOf<GameLifecycle>()

    // 注册 RoundLifecycle 实例
    fun register(lifecycle: RoundLifecycle) {
        roundLifecycles.add(lifecycle)
    }
    fun register(lifecycle: RoundEnd) {
        roundEndLifecycles.add(lifecycle)
    }

    // 新增：接受 Any 类型参数的 register 方法
    override fun register(lifecycle: Any) {
        if (lifecycle !is Lifecycle) return
        if (lifecycle is RoundLifecycle) {
            roundLifecycles.add(lifecycle)
        }
        if (lifecycle is GameLifecycle) {
            gameLifecycles.add(lifecycle)
        }
        if (lifecycle is RoundEnd) {
            roundEndLifecycles.add(lifecycle)
        }

    }

    override fun logouts(anyList: List<Any>) {
        anyList.forEach { any ->
            logout(any)
        }

    }

    fun logout(any: Any) {
        if (any is RoundLifecycle) {
            roundLifecycles.remove(any)
        }
        if (any is GameLifecycle) {
            gameLifecycles.remove(any)
        }
        if (any is RoundEnd) {
            roundEndLifecycles.remove(any)
        }
    }

    // 注册 GameLifecycle 实例
    fun register(lifecycle: GameLifecycle) {
        gameLifecycles.add(lifecycle)
    }

    // 触发所有 RoundLifecycle 的 start 方法
    fun startAllRuleLifecycles(warInfo: WarInfo) {
        roundLifecycles.forEach { it.start(warInfo) }
    }
    fun endRound(warInfo: WarInfo) {
        roundEndLifecycles.forEach { it.end(warInfo) }
    }


    // 触发所有 GameLifecycle 的 start 方法
    fun startAllGameLifecycles() {
        gameLifecycles.forEach { it.start() }
    }

}