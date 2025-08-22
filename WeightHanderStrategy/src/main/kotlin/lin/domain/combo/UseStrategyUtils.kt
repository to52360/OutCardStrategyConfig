package lin.domain.combo

import lin.domain.context.FourAnimationTime
import lin.myLog
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * 直接阻塞实现就好了
 */
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
                if (count != 0L) {
                    myLog.info { "进入同步,等待发现" }
                    await(FourAnimationTime, TimeUnit.MILLISECONDS)
                }
                myLog.info { "阻塞等待发现操作" }
                //等待发现动画
                Thread.sleep(FourAnimationTime)
            }
        }
        clean()
    }

    fun down() {
        countDownLatch?.run {
            myLog.info { "唤醒等待发现" }
            countDown()
        }
    }

    fun clean() {
        countDownLatch = null
    }

}