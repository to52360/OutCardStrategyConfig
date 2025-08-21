package lin.domain.combo

import lin.domain.context.ThreeAnimationTime
import lin.myLog
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UseStrategyUtils {
    var countDownLatch: CountDownLatch? = null
    var useResult: Boolean = false
    fun register() {
        myLog.info { "注册发现" }
        countDownLatch = CountDownLatch(1)
    }

    fun await() {
        if (useResult) {
            countDownLatch?.run {
                myLog.info { "进入阻塞,等待发现" }
                await(ThreeAnimationTime, TimeUnit.MILLISECONDS)
                myLog.info { "阻塞等待发现动画" }
                //等待发现动画
                Thread.sleep(ThreeAnimationTime)

            }
        }
        clean()
    }

    fun down() {
        countDownLatch?.run {
            myLog.info { "唤醒发现" }

            countDown()
        }
    }

    fun clean() {
        myLog.info { "清除发现" }
        countDownLatch = null
    }

}