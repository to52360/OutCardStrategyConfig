package lin.lifecycle

import lin.domain.WarInfo

/**
 * 适应用于
 * [lin.serviceLoader.weightRule.WeightRule]
 * [lin.weightHandler.WeightHandler]
 * 回合开始
 */
sealed interface Lifecycle

interface RoundLifecycle : Lifecycle {
    fun start(warInfo: WarInfo)
}

interface RoundEnd : Lifecycle {
    /**
     * todo-future 并发修改错误 临时解决方案,返回ture移除
     */
    fun end(warInfo: WarInfo): Boolean
}

/**
 * 游戏开始
 */
interface GameLifecycle : Lifecycle {
    fun start()
}