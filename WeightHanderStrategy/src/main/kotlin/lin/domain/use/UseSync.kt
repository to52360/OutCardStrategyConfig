package lin.domain.use

import lin.domain.context.UseAnimationTime
import lin.myLog
import java.util.concurrent.atomic.AtomicInteger

class UseSync {
    val baseNum = 0
    val completedCount = AtomicInteger(baseNum) // 线程安全的完成计数器

    val maxRetries = 5
    val waitInterval = UseAnimationTime


    fun tryWait() {
        if (completedCount.get() == baseNum) return
        myLog.info { "尝试等待发现动作" }
        var attempt = 0
        while (attempt < maxRetries) {
            Thread.sleep(waitInterval)
            val result = completedCount.decrementAndGet()
            if (result <= 0) {
                if (result < 0) {
                    myLog.warn { "非法状态异常,预期之外的状态" }
                    completedCount.set(baseNum)
                }
                break
            }

            attempt++
        }

        //超过次数直接设置为0
        myLog.warn { "超次数设置为0" }
        completedCount.set(baseNum)


    }

    fun tryRegister() {
        myLog.info { "注册发现动作" }
        completedCount.incrementAndGet()
    }
}