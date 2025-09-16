package lin.lifecycle

import lin.domain.WarInfo

/**
 * 适应用于
 * [lin.serviceLoader.weightRule.WeightRule]
 * [lin.weightHandler.WeightHandler]
 * 回合开始
 */
interface RoundLifecycle {
    fun start(warInfo: WarInfo)
}

/**
 * 游戏开始
 */
interface GameLifecycle {
    fun start()
}