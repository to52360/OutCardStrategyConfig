package lin.domain


import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import lin.bean.AfterStrategy
import lin.bean.ComboCard
import lin.lifecycle.LifecycleRegister
import lin.lifecycle.LifecycleRegisterImpl
import lin.myLog
import lin.utils.JarClassLoader
import lin.warExt.base.getNowCost
import lin.warExt.base.hasCost
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module


/**
 * todo-future 名字还没有想好
 * 参考数据
 * 执行攻击动作
 * [club.xiaojiawei.util.DeckStrategyUtil.Result.execAction]
 * mapstruct DaoDao复制 Mapper
 *
 * []
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */

class ComboDomain(war: War) {

    //SPI没法抛异常把把val 改为 lateinit var
    //存储转化权重信息
    private lateinit var warManage: MyWarManage
    private lateinit var weightHandlerDomain: WeightHandlerDomain
    private val lifecycleRegisterImpl = LifecycleRegisterImpl()
    private val classLoader = JarClassLoader(parent = javaClass.classLoader).classLoader() ?: run {
        log.warn { "没有获取到类加载器" }
        javaClass.classLoader
    }

    //存储策略分组
    init {
        myLog.info {
            "ComboDao初始化"
        }
        threadContext {

            startKoin {
                modules(DBModules)
                modules(module { single { lifecycleRegisterImpl } bind LifecycleRegister::class })
            }
            Thread.currentThread().contextClassLoader = classLoader
            warManage = MyWarManage(war)
            weightHandlerDomain = WeightHandlerDomain(warManage = warManage)
        }
    }

    private inline fun threadContext(runnable: () -> Unit) {
        val threadClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = classLoader
            runnable()
        } catch (t: Throwable) {
            myLog.error(t) { "全局错误捕获" }
            throw t
        } finally {
            Thread.currentThread().contextClassLoader = threadClassLoader
        }
    }

    private inline fun executeEnvironment(runnable: () -> Unit) {
        threadContext {
            lifecycleRegisterImpl.startAllRuleLifecycles()
            val isStart = warManage.isStart()
            if (isStart) {
                lifecycleRegisterImpl.startAllGameLifecycles()
            }
            warManage.executeEnvironment {
                runnable()

            }
        }

    }

    /**
     * 出牌策略
     */
    fun outCardStrategy() {
        myLog.info { "执行出牌策略" }
        executeEnvironment {
            findAndUse()
        }
    }

    private fun findAndUse() {
        val weightResult = weightHandlerDomain.findCombination()
        when (weightResult) {
            is EmptyWeightResult -> return
            is EndWeightResult -> {
                myLog.info { "找到需要使用的卡牌:${weightResult.bestCombination}" }
                executeUseCard(weightResult)
            }
        }

    }



    /**
     *
     */
    private fun executeUseCard(weightResult: EndWeightResult) {
        val bestCombination = weightResult.bestCombination
        // 5. 执行找到的最佳出牌组合
        if (bestCombination.isNotEmpty()) {

            //只有一个处理
            if (bestCombination.size == 1) {
                useCardAndIsReload(bestCombination.first())
                return
            }


            val needCost = bestCombination.sumOf { it.getCost() }

            //todo-future  打出优先级处理 combo情况处理
            val bestCombinationCombo = bestCombination.sortedByDescending {
                it.powerWeight
            }
            myLog.info {
                val finalWeight = bestCombinationCombo.sumOf { it.powerWeight }
                val msg =
                    "找到最优出牌组合 (总费用: $needCost, 总权重: $finalWeight): $bestCombinationCombo"
                msg
            }


            val expectCost = warManage.getNowCost() - needCost

            //todo-future 这里使用策略有问题,要扩展要改源码
            val afterUse = sortedSetOf<ComboCard>(compareByDescending { it.powerWeight })//之后使用
            for (card in bestCombinationCombo) {
                if (AfterStrategy == card.useStrategy) {
                    myLog.info { "id:${card.cardId()},name:${card.card.entityName}添加到最后打出" }
                    afterUse.add(card)
                } else {
                    if (useCardAndIsReload(card)) return
                }
            }
            if (afterUse.isNotEmpty()) {
                afterUse.forEach { if (useCardAndIsReload(it)) return }
            }
            //todo-future 直接遍历使用
            //todo 还存在问题 ,万一新增卡牌
            if (warManage.getNowCost() > expectCost) {//说明有些牌没打出去,通过补偿
                val moreTryCard = weightResult.lessAbleUseCards()
                useCards(moreTryCard)


            }
        }
    }

    fun useCards(comboCards: List<ComboCard>) {
        if (comboCards.isNotEmpty()) {
            for (card in comboCards) {
                if (warManage.getNowCost() >= card.getCost()) {
                    warManage.useCard(card)
                    if (!warManage.hasCost()) break
                }
            }
        }
    }


    fun useCardAndIsReload(card: ComboCard): Boolean {
        val useResult = warManage.useCard(card)
        myLog.info { "使用结果:$useResult" }
        if (useResult) {
            //todo 暂时使用休眠
            Thread.sleep(3000)
            val isBreak = isReload()
            myLog.info { "是否有变化:$isBreak" }
            return isBreak
        }
        return false
    }

    /**
     * 刷新重新调用回溯查找 需要配合[MyWarManage.useCard]使用
     * todo-future 先验证可行性
     *
     */
    private fun isReload(): Boolean {
        val change = warManage.changeAndReload()
        if (change) {
            myLog.info { "有变化,重新执行" }
            findAndUse()

        }
        return change
    }

    fun executeDiscoverChooseCard(vararg cards: Card): Int {
        var index = 0
        threadContext {
            index = weightHandlerDomain.executeDiscoverChooseCard(*cards)
        }

        return index
    }

}




