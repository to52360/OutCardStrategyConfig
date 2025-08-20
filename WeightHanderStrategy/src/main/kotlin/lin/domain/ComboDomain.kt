package lin.domain


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.War
import club.xiaojiawei.hsscriptcardsdk.data.BaseData
import lin.bean.ComboCard
import lin.bean.UseAfterStrategy
import lin.bean.UseBeforeStrategy
import lin.domain.combo.ChangeWeightResult
import lin.domain.combo.EmptyWeightResult
import lin.domain.combo.EndWeightResult
import lin.domain.combo.UseStrategyUtils
import lin.domain.context.AwaitAnimationTime
import lin.domain.context.CostWeight
import lin.domain.context.NotWeight
import lin.domain.context.UseSkillWeight
import lin.lifecycle.LifecycleRegister
import lin.lifecycle.LifecycleRegisterImpl
import lin.myLog
import lin.utils.serviceLoader.JarClassLoader
import lin.warExt.base.getNowCost
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
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
        myLog.warn { "没有获取到类加载器" }
        javaClass.classLoader
    }
    private var unAbleUseCards: TreeSet<ComboCard>? = null
    private val useStrategyUtils = UseStrategyUtils()

    //存储策略分组
    init {
        myLog.info {
            "ComboDao初始化"
        }
        threadContext {

            startKoin {
                modules(DBModules, ParseCardWeightInfoModule)
                modules(module { single { lifecycleRegisterImpl } bind LifecycleRegister::class })
            }
            Thread.currentThread().contextClassLoader = classLoader
            warManage = MyWarManage(war)
            weightHandlerDomain = WeightHandlerDomain(warManage = warManage)
            stopKoin()
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

    private fun processLessCost(): Boolean {
        val costWeight = CostWeight * warManage.getNowCost()

        if (costWeight == NotWeight) return false
        val skillComboCard: ComboCard? = warManage.war.me.playArea.power?.let {
            if (costWeight + UseSkillWeight < 0) return@let null
            val skill = ComboCard(null, it)
            skill.addWeight(UseSkillWeight)
            skill
        }
        unAbleUseCards?.let { unAbleUseCards ->
            skillComboCard?.also {
                unAbleUseCards.add(it)
            }
            for (unAbleUseCard in unAbleUseCards) {
                if (costWeight + unAbleUseCard.powerWeight < 0) return false
                if (unAbleUseCard.cost() <= warManage.getNowCost()) {
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

    /**
     * 出牌策略
     */
    fun outCardStrategy() {
        myLog.info { "执行出牌策略" }
        executeEnvironment {
            findAndUse()
            if (processLessCost()) return
        }
    }

    private fun findAndUse() {
        val weightResult = weightHandlerDomain.findCombination()
        when (weightResult) {
            is EmptyWeightResult -> return
            is EndWeightResult -> {
                myLog.info { "找到需要使用的卡牌:${weightResult.bestCombination}" }
                unAbleUseCards = weightResult.unUseCards
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


            val needCost = bestCombination.sumOf { it.cost() }

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
            var lastUse: SortedSet<ComboCard>? = null
            for (card in bestCombinationCombo) {
                card.lastUse?.let {
                    lastUse?.run {
                        add(card)
                    } ?: run {
                        lastUse = sortedSetOf(
                            compareByDescending<ComboCard> { it.lastUse!!.comboWeight }.thenBy {
                                it.card.entityId
                            }
                        )
                        lastUse.add(card)
                    }
                    myLog.info { "id:${card.cardId()},name:${card.card.entityName}添加到最后打出" }
                } ?: run {
                    if (useCardAndIsReload(card)) return
                }
            }
            if (lastUse != null) {
                for (lastUseCard in lastUse) {
                    if (useCardAndIsReload(lastUseCard)) return
                }
            }

            //todo 还存在问题 ,万一新增卡牌
            if (warManage.getNowCost() > expectCost) {//说明有些牌没打出去,通过补偿
                val moreTryCard = weightResult.lessAbleUseCards()
                if (moreTryCard.isNotEmpty()) {
                    for (moreCard in moreTryCard) {
                        if (useCardAndIsReload(moreCard)) return
                    }
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
        card.useBeforeStrategy?.executeAction(card, useStrategyUtils)
        val useResult = warManage.useCard(card)
        myLog.info { "打出$card,使用结果:$useResult" }
        useStrategyUtils.useResult = useResult
        card.useAfterStrategy?.executeAfterAction(card, useStrategyUtils)
        if (useResult) {
            myLog.info { "打出等待动画" }
            Thread.sleep(AwaitAnimationTime)
            val isBreak = isReload()
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
            myLog.info { "有变化,重新查询combo" }
            findAndUse()

        }
        return change
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




