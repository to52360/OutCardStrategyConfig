package lin.lifecycle

/**
 * 适应用于
 * [lin.serviceLoader.weightRule.WeightRule]
 * [lin.weightHandler.WeightHandler]
 * 回合开始
 */
interface RoundLifecycle {
    fun start()
}

/**
 * 游戏开始
 */
interface GameLifecycle {
    fun start()
}