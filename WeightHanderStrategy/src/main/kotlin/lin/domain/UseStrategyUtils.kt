package lin.domain

import club.xiaojiawei.config.log
import lin.myLog
import lin.weightHandler.condition.context.DealyActionTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UseStrategyUtils {
    var countDownLatch: CountDownLatch? = null
    var useResult: Boolean = false
    fun register() {
        log.info { "注册发现" }
        countDownLatch = CountDownLatch(1)
    }

    fun await() {
        if (useResult) {
            log.info { "进入阻塞,等待发现" }
            countDownLatch?.await(DealyActionTime, TimeUnit.MILLISECONDS)
        } else clean()
    }

    fun down() {
        countDownLatch?.run {
            countDown()
            clean()
        }
    }

    fun clean() {
        myLog.info { "清除发现" }
        countDownLatch = null
    }

}