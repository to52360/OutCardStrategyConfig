package lin.domain


import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.data.BaseData
import lin.bean.ComboCard
import lin.domain.context.AwaitAnimationTime
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.domain.context.UseSkillWeight
import lin.domain.result.ChangeWeightResult
import lin.domain.result.EndWeightResult
import lin.domain.strategy.UseAfterStrategy
import lin.domain.strategy.UseBeforeStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.lifecycle.LifecycleRegisterImpl
import lin.myLog
import lin.utils.serviceLoader.JarClassLoader
import lin.warExt.my.base.getCost
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*


/**
 * todo-future 名字还没有想好
 * 参考数据
 * 执行攻击动作
 * club.xiaojiawei.util.DeckStrategyUtil.Result.execAction
 * mapstruct DaoDao复制 Mapper
 *
 * [club.xiaojiawei.hsscript.utils.GameUtil]
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */
const val MaxStackNum: Int = 10

class ComboDomain : KoinComponent {


    //SPI没法抛异常把把val 改为 lateinit var
    //存储转化权重信息
    private lateinit var warManage: MyWarManage
    private lateinit var weightHandlerDomain: WeightHandlerDomain
    private val lifecycleRegisterImpl = LifecycleRegisterImpl()
    private val classLoader = JarClassLoader(parent = javaClass.classLoader).classLoader() ?: run {
        myLog.warn { "没有获取到类加载器" }
        javaClass.classLoader
    }
    private var unAbleUseCards: TreeSet<ComboCard>? = null
    private val useStrategyUtils = get<UseStrategyUtils>()

    //存储策略分组
    init {
        myLog.info {
            "ComboDao初始化"
        }
        threadContext {
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

    private inline fun executeEnvironment(runnable: () -> Unit) {
        //重置状态
        stackNum = 0

        processSkill()


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

    //todo-future 有空封装起来
    private var skillComboCard: ComboCard? = null
    fun processSkill() {
        fun createSkill() {
            skillComboCard = warManage.war.me.playArea.power?.let {
                val skill = ComboCard(null, it)
                skill.addWeight(UseSkillWeight)
                skill
            }
        }
        skillComboCard?.let {
            val skill = warManage.war.me.playArea.power
            if (it.card != skill) { //技能变更,重新缓存
                createSkill()
            }
        } ?: run {//没有缓存就进行缓存
            createSkill()
        }

    }

    /**
     * 处理剩余费用
     */
    private fun processLessCost(): Boolean {
        myLog.info { "处理剩余费用,处理列表:${unAbleUseCards}" }
        val costWeight = CostWeight * warManage.getCost()

        //todo-future 可能报纸不打零费牌
        if (costWeight == NotWeight) return false

        unAbleUseCards?.let { unAbleUseCards ->
            skillComboCard?.also {
                unAbleUseCards.add(it)
            }
            for (unAbleUseCard in unAbleUseCards) {
                if (costWeight + unAbleUseCard.powerWeight < NotWeight) return false
                if (unAbleUseCard.cost() <= warManage.getCost()) {
                    if (useCardAndIsReload(unAbleUseCard)) return true
                }
            }
        } ?: run {
            skillComboCard?.also {
                return useCardAndIsReload(it)
            }
        }

        return false
    }
    private var stackNum = 0
    /**
     * 出牌策略
     */
    fun outCardStrategy() {
        myLog.info { "执行出牌策略" }
        executeEnvironment {
            findAndUse()
            processLessCost()
        }
    }

    /**
     * 该方法会循环调用
     */
    private fun findAndUse() {
        findAndUseStatusProcess {
            val weightResult = weightHandlerDomain.findCombination()

            if (weightResult is EndWeightResult) {
                myLog.info { "找到需要使用的卡牌:${weightResult.bestCombination}" }
                unAbleUseCards = weightResult.unUseCards
                executeUseCard(weightResult)
            }
        }
    }

    private inline fun findAndUseStatusProcess(runnable: () -> Unit) {
        if (stackNum == MaxStackNum) {
            log.warn { "栈过深" }
            return
        } else
            stackNum++
        unAbleUseCards = null
        runnable()
    }



    /**
     *
     */
    private fun executeUseCard(weightResult: EndWeightResult) {
        val bestCombination = weightResult.bestCombination
        if (bestCombination.isEmpty()) return
        // 5. 执行找到的最佳出牌组合
        //只有一个处理
        if (bestCombination.size == 1) {
            useCardAndIsReload(bestCombination.first())
            return
        }


        val needCost = bestCombination.sumOf { it.cost() }
        val expectCost = warManage.getCost() - needCost

        /**
         * todo-future 这个排序有重,可以根据不同上下切换
         * 上下文判断入口
         * [MyWarManage.parseCombo]
         */
        val bestCombinationCombo =
            bestCombination.sortedWith(compareBy<ComboCard> { it.useGroupId }.thenBy { it.useGroupOrder }
                .thenComparing { it.powerWeight })

        myLog.info {
            val finalWeight = bestCombinationCombo.sumOf { it.powerWeight }
            val msg =
                "找到最优出牌组合 (总费用: $needCost, 总权重: $finalWeight): $bestCombinationCombo"
            msg
        }

        for (card in bestCombinationCombo) {
            if (useCardAndIsReload(card)) return
        }

        if (warManage.getCost() > expectCost) {//说明有些牌没打出去,通过补偿
            val moreTryCard = weightResult.lessAbleUseCards()
            if (moreTryCard.isNotEmpty()) {
                for (moreCard in moreTryCard) {
                    if (useCardAndIsReload(moreCard)) return
                }
            }
        }



    }

    fun List<UseBeforeStrategy>.executeAction(card: ComboCard, useStrategyUtils: UseStrategyUtils) {
        this.forEach {
            it.extAction(card, useStrategyUtils)
        }
    }

    fun List<UseAfterStrategy>.executeAfterAction(card: ComboCard, useStrategyUtils: UseStrategyUtils) {
        this.forEach {
            it.afterExtAction(card, useStrategyUtils)
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
                Thread.sleep(AwaitAnimationTime)
                card
            } else null
        }
        if (changeResult) {
            myLog.info { "有变化,重新查询combo,等待变化动画" }
            Thread.sleep(AwaitAnimationTime)
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




