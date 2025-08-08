package lin.domain


import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import lin.bean.AddCostStrategy
import lin.bean.AfterStrategy
import lin.bean.ChangeStrategy
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
import java.util.*


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
        try {
            threadContext {

                startKoin {
                    modules(DBModules)
                    modules(module { single { lifecycleRegisterImpl } bind LifecycleRegister::class })
                }
                Thread.currentThread().contextClassLoader = classLoader
                warManage = MyWarManage(war)
                weightHandlerDomain = WeightHandlerDomain(warManage = warManage)
            }

        } catch (serviceError: ServiceConfigurationError) {
            serviceError.printStackTrace()
            myLog.error(serviceError) { "serviceError初始化失败" }
        }


    }

    private inline fun threadContext(runnable: () -> Unit) {
        val threadClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = classLoader
            runnable()
        } finally {
            Thread.currentThread().contextClassLoader = threadClassLoader
        }
    }

    private inline fun executeEnvironment(runnable: () -> Unit) {
        lifecycleRegisterImpl.startAllRuleLifecycles()
        val isStart = warManage.isStart()
        if (isStart) {
            lifecycleRegisterImpl.startAllGameLifecycles()
        }
        warManage.executeEnvironment { runnable() }
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
        //获取能够打出的卡牌
        val canUseCardsByCost = warManage.canUseCards
        //todo 看一下isChange能不能放入weightHandlerDao
        val bestCombination = findBestCombination(canUseCardsByCost, false)

        val canUseCardsByHandler = weightHandlerDomain.readCanUseCardsByHandler
        myLog.info { "找到需要使用的卡牌:$bestCombination" }
        executeUseCard(canUseCardsByHandler, bestCombination)
    }

    /**
     *  处理使用策略,权重转发给权重处理器模型处理
     */
    private fun findBestCombination(cards: List<ComboCard>, isChange: Boolean): List<ComboCard> {
        if (isChange) {//todo 这方案不太靠谱 重新计算权重并使用
            weightHandlerDomain.executeHandChaWeightProcess(cards)
        } else {
            weightHandlerDomain.executeWeightProcess(cards)
        }

        val canUseCardsByHandler = weightHandlerDomain.findBeforeOrBestCombination()

        when (canUseCardsByHandler.size) {
            0 -> return emptyList()
            1 -> {
                val comboCard = canUseCardsByHandler.first()
                when (val useStrategy = comboCard.useStrategy) {
                    ChangeStrategy -> {
                        warManage.useCardAndRemove(canUseCardsByHandler.first())
                        warManage.refreshComboCards()
                        //todo 这方案不太靠谱 重新计算权重并使用
                        return findBestCombination(warManage.canUseCards, true)
                    }

                    is AddCostStrategy -> {
                        val expectCost = useStrategy.cost + warManage.getNowCost()
                        return weightHandlerDomain.findBestCombinationByExpectCost(comboCard, expectCost)
                    }

                    else -> return canUseCardsByHandler
                }
            }

            else -> {
                return canUseCardsByHandler
            }

        }

    }

    /**
     * @param canUseCardsByHandler 全部能打的卡牌
     * @param bestCombination 回溯算法获取组合
     */
    private fun executeUseCard(canUseCardsByHandler: List<ComboCard>, bestCombination: List<ComboCard>) {
        // 5. 执行找到的最佳出牌组合
        if (bestCombination.isNotEmpty()) {

            //只有一个处理
            if (bestCombination.size == 1) {
                useCardAndIsBreak(bestCombination.first())
                return
            }


            val needCost = bestCombination.sumOf { it.getCost() }
            myLog.info {
                val finalWeight = bestCombination.sumOf { it.powerWeight }
                val msg =
                    "找到最优出牌组合 (总费用: $needCost, 总权重: $finalWeight): ${bestCombination.map { it.card.cardId }}"
                msg
            }

            //todo-future  打出优先级处理 combo情况处理
            val bestCombinationCombo = bestCombination.sortedByDescending {
                it.powerWeight
            }


            val expectCost = warManage.getNowCost() - needCost
            //todo-fu这里使用
            val afterUse = sortedSetOf<ComboCard>()//之后使用
            for (card in bestCombinationCombo) {
                if (AfterStrategy == card.useStrategy) {
                    afterUse.add(card)
                } else {
                    if (useCardAndIsBreak(card)) return
                }
            }
            if (afterUse.isNotEmpty()) {
                afterUse.forEach { if (useCardAndIsBreak(it)) return }
            }
            //todo-future 直接遍历使用
            //todo 还存在问题 ,万一新增卡牌
            if (warManage.getNowCost() > expectCost) {//说明有些牌没打出去,通过补偿
                val moreTryCard = canUseCardsByHandler - bestCombination.toSet()
                if (moreTryCard.isNotEmpty()) {
                    for (card in moreTryCard) {
                        if (warManage.getNowCost() >= card.getCost()) {
                            warManage.useCard(card)
                            if (!warManage.hasCost()) break
                        }
                    }
                }


            }
        }
    }


    fun useCardAndIsBreak(card: ComboCard): Boolean {
        val useResult = warManage.useCard(card)
        myLog.info { "使用结果:$useResult" }
        if (useResult) {
            val isBreak = isBreak()
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
    private fun isBreak(): Boolean {
        if (warManage.hasCost()) {
            val change = warManage.changeAndReload()
            if (change) {
                myLog.info { "有变化,重新执行" }
                findAndUse()

            }
            return change
        } else {
            return false
        }

    }

    fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return weightHandlerDomain.executeDiscoverChooseCard(*cards)
    }

}




