package lin.domain.use

import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.domain.context.ChangeAnimationTime
import lin.domain.context.FourAnimationTime
import lin.domain.context.UseAnimationTime
import lin.myLog
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getHandCards
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * 直接阻塞实现就好了,用来控制并发
 */
class UseDomain(val warManage: MyWarManage) {
    var countDownLatch: CountDownLatch? = null
    private val useSync: UseSync = UseSync()
    var reFindCombo = false
    var isChange = false
        private set
    var extAwait: Long = 0


    fun reset() {
        //todo-future 临时方案 切换为使用上下文,跟线程协作写在一起不好分开,还没想好怎么处理
        extAwait = 0
        reFindCombo = false
        isChange = false
        useResult = false
    }

    fun useCard(card: ComboCard) {
        reset()
        isChange = warManage.isChangeByUseSuccess {
            card.useBeforeStrategy?.executeAction(card, this)
            if (reFindCombo) {
                null
            }
            useResult = warManage.tryUseCard(card)
            myLog.info { "打出$card,使用结果:$useResult" }
            card.useAfterStrategy?.executeAfterAction(card, this)
            if (reFindCombo) {
                null
            }
            if (useResult) {
                //超过指定测试应该不要等待时间了
                myLog.info { "打出等待动画" }
                Thread.sleep(UseAnimationTime + extAwait)
                //select 暂时这样处理发现,看一下有没有问题
                tryAwait()
                card
            } else null
        }
        //todo-future 临时方案 重新查暂定也用change的方案
        if (reFindCombo) {
            isChange = true
        }
        if (isChange) {
            if (!reFindCombo) {
                myLog.info { "有变化,重新查询combo,等待变化动画" }
                Thread.sleep(ChangeAnimationTime)
            }
            val existAbleUse = warManage.getHandCards().any { it.cost <= warManage.getCost() }
            //没有可用牌就不再查了
            if (!existAbleUse) {
                myLog.info { "无可用牌不执行重新查找combo" }
                isChange = false
            }
        }
    }


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
                Thread.sleep(ChangeAnimationTime)
                clean()
                return
            }
            return
        }

    }

    /**
     * 暂时不做额外处理,
     * 发现动作代码comboCard.card.action.chooseOne(0)
     */
    fun tryAwait() {
        countDownLatch ?: useSync.tryWait()

    }

    /**
     * 没有定义的时候处理
     */
    fun tryRegister() {
        countDownLatch ?: useSync.tryRegister()
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
