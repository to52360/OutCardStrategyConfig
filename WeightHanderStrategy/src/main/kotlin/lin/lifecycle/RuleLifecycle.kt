package lin.lifecycle

/**
 * 回合开始
 */
interface RuleLifecycle {
    fun start()
}

/**
 * 游戏开始
 */
interface RuleGameLifecycle {
    fun start()
}