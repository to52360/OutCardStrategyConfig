package lin.domain


import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.data.BaseData
import lin.bean.ComboCard
import lin.domain.context.ChangeAnimationTime
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.domain.context.UseAnimationTime
import lin.domain.result.*
import lin.domain.strategy.*
import lin.lifecycle.LifecycleRegister
import lin.lifecycle.LifecycleRegisterImpl
import lin.myLog
import lin.utils.serviceLoader.JarClassLoader
import lin.warExt.my.base.getCost
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.dsl.bind
import org.koin.dsl.module


/**
 * 职责查找combo,使用combo
 */
const val MaxStackNum: Int = 10

class ComboDomain : KoinComponent {
    companion object {
        val USE_ORDER = compareBy<ComboCard> { it.useGroupId }.thenByDescending { it.useGroupOrder }
    }

    //SPI没法抛异常把把val 改为 lateinit var
    //存储转化权重信息
    private lateinit var warManage: MyWarManage
    private lateinit var weightHandlerDomain: WeightHandlerDomain
    private val lifecycleRegisterImpl = LifecycleRegisterImpl()
    private val classLoader = JarClassLoader(parent = javaClass.classLoader).classLoader() ?: run {
        myLog.warn { "没有获取到类加载器" }
        javaClass.classLoader
    }
    private val useStrategyUtils = get<UseStrategyUtils>()
    private val findComboStrategyList = getKoin().getAll<FindComboStrategy>().sortedBy { it.priority() }
    private val findPlanner = get<FindPlanner>()

    //存储策略分组
    init {
        myLog.info {
            "ComboDao初始化"
        }
        threadContext {
            loadKoinModules(module {
                single { lifecycleRegisterImpl } bind LifecycleRegister::class
            })
            warManage = get<MyWarManage>()
            weightHandlerDomain = get<WeightHandlerDomain>()
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
    private var stackNum = 0

    private inline fun executeEnvironment(runnable: () -> Unit) {
        //重置状态
        stackNum = 0




        //生命周期
        threadContext {
            lifecycleRegisterImpl.startAllRuleLifecycles(warManage)
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
     * 处理剩余费用
     */
    private fun processLessCost(weightResult: EndWeightResult): Boolean {
        //记录剩余的卡
        val unAbleUseCards = weightResult.unUseCards
        myLog.info { "权重为负的卡:${unAbleUseCards}" }
        //todo 这里存在问题
        val moreTryCard = weightResult.lessAbleUseCards()
        if (moreTryCard.isNotEmpty()) {
            unAbleUseCards.addAll(moreTryCard)
        }
        val costWeight = CostWeight * warManage.getCost()

        //没有为0的牌
        if (costWeight == NotWeight && !unAbleUseCards.any { it.cost() == 0 }) return false

        unAbleUseCards.removeIf { it.cost() > warManage.getCost() || costWeight + it.powerWeight < NotWeight }
        if (unAbleUseCards.isEmpty()) return false

        val bestCombos = DefaultFindBestCombination.findBestCombination(unAbleUseCards, warManage.getCost())
            .sortedWith(USE_ORDER)
        for (bestCombo in bestCombos) {
            if (useCardAndIsReload(bestCombo)) return true
        }



        return false
    }

    /**
     * 出牌策略
     */
    fun outCardStrategy() {
        myLog.info { "执行出牌策略" }
        executeEnvironment {
            findAndUse()
            //processLessCost()
        }
    }

    /**
     * 该方法会循环调用
     */
    private fun findAndUse() {
        findAndUseTransaction {
            var weightPlanner: CmdPlanner = ContinuePlanner
            for (findComboStrategy in findComboStrategyList) {
                weightPlanner = when (weightPlanner) {
                    is ContinuePlanner -> findComboStrategy.find(findPlanner)
                    is ResultPlanner -> findComboStrategy.find(findPlanner, weightPlanner.weightResult)
                        .toPlanner()
                }
            }
            if (weightPlanner is ResultPlanner) {
                val weightResult = weightPlanner.weightResult
                when (weightResult) {
                    is EndWeightResult -> {
                        myLog.info { "找到需要使用的卡牌:${weightResult.bestCombination}" }
                        executeUseCard(weightResult)
                    }

                    is EmptyWeightResult -> {
                        log.info { "没有可用卡牌,剩余费用:${warManage.getCost()}" }
                    }
                }
            }

        }

    }

    /**
     * 查询和使用的事务
     */
    private inline fun findAndUseTransaction(runnable: () -> Unit) {
        if (stackNum == MaxStackNum) {
            log.warn { "栈过深" }
            return
        } else
            stackNum++

        runnable()
    }



    /**
     *todo
     */
    private fun executeUseCard(weightResult: EndWeightResult) {
        val bestCombination = weightResult.bestCombination
        myLog.info { "能够使用的卡牌:${weightResult.lessAbleUseCards()}" }
        val result = useCombo(bestCombination)
        //重新执行
        if (result) return

        processLessCost(weightResult)



    }

    /**
     * @return false 为执行完毕,true为重新调用
     */
    fun useCombo(bestCombination: List<ComboCard>): Boolean {

        if (bestCombination.isEmpty()) return false
        // 5. 执行找到的最佳出牌组合
        //只有一个处理
        if (bestCombination.size == 1) {
            useCardAndIsReload(bestCombination.first())
            return false
        }


        val needCost = bestCombination.sumOf { it.cost() }

        /**
         * todo-future 这个排序有重,可以根据不同上下切换
         * 上下文判断入口
         * [MyWarManage.parseCombo]
         */
        val bestCombinationCombo =
            bestCombination.sortedWith(USE_ORDER)

        myLog.info {
            val finalWeight = bestCombinationCombo.sumOf { it.powerWeight }
            val msg =
                "找到最优出牌组合 (总费用: $needCost, 总权重: $finalWeight): $bestCombinationCombo"
            msg
        }

        for (card in bestCombinationCombo) {
            if (useCardAndIsReload(card)) return true
        }
        return false
    }


    fun List<UseBeforeStrategy>.executeAction(card: ComboCard, useStrategyUtils: UseStrategyUtils) {
        this.forEach {
            it.extAction(card, useStrategyUtils, warManage)
        }
    }

    fun List<UseAfterStrategy>.executeAfterAction(card: ComboCard, useStrategyUtils: UseStrategyUtils) {
        this.forEach {
            it.afterExtAction(card, useStrategyUtils, warManage)
        }
    }
    fun useCardAndIsReload(card: ComboCard): Boolean {
        val changeResult = warManage.isChange {
            card.useBeforeStrategy?.executeAction(card, useStrategyUtils)
            val useResult = warManage.tryUseCard(card)
            myLog.info { "打出$card,使用结果:$useResult" }
            useStrategyUtils.useResult = useResult
            card.useAfterStrategy?.executeAfterAction(card, useStrategyUtils)
            if (useResult) {
                myLog.info { "打出等待动画" }
                Thread.sleep(UseAnimationTime)
                card
            } else null
        }
        if (changeResult) {
            myLog.info { "有变化,重新查询combo,等待变化动画" }
            Thread.sleep(ChangeAnimationTime)
            warManage.reLoad()
            findAndUse()
        }
        return changeResult
    }

    fun executeChangeCard(cards: HashSet<Card>) {
        threadContext {
            if (BaseData.enableChangeWeight) {
                val changeWeightResult = ChangeWeightResult(cards, warManage.parseComboCards(cards.toList()))
                changeWeightResult.processChangeCard()

            } else {
                cards.removeIf { card -> card.cost > 2 }
            }
        }

    }

    fun executeDiscoverChooseCard(vararg cards: Card): Int {
        try {
            useStrategyUtils.tryRegister()
            var index = 0
            threadContext {
                index = weightHandlerDomain.executeDiscoverChooseCard(*cards)
            }
            return index
        } finally {
            useStrategyUtils.down()
        }

    }

}




