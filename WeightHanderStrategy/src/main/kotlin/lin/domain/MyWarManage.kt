package lin.domain


import club.xiaojiawei.hsscriptbasestrategy.util.DeckStrategyUtil
import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.War
import club.xiaojiawei.hsscriptcardsdk.bean.area.HandArea
import club.xiaojiawei.hsscriptcardsdk.bean.isValid
import club.xiaojiawei.hsscriptcardsdk.data.CARD_INFO_TRIE
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.lifecycle.LifecycleRegister
import lin.lifecycle.LifecycleRegisterImpl
import lin.myLog
import lin.serviceLoader.cardInfoProvide.CardWeightInfoProvide
import lin.serviceLoader.parse.ParseCardWeightInfo
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.action.activeLocation
import lin.warExt.action.cleanPlay
import lin.warExt.action.cleanPlayAll
import lin.warExt.my.base.*
import lin.weightHandler.warHandler.ToDieHandler
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


interface WarInfo {
    val war: War

    //手牌
    val handComboCards: List<ComboCard>

    //能够使用的手牌
    val canUseCards: List<ComboCard>

    //我方战场区域的牌
    val playComboCards: List<ComboCard>

    //权重信息
    val infoMap: Map<String, CardWeightInfo>

    val extCost: Int

    /**
     * 用于重复调用,但是每回合只能调用一次
     * @return 为true就执行过了
     */
    fun cleanPlayByRoundOnce(): Boolean

    /**
     * 没回合执行一次
     * @return 为ture表示执行过了
     */
    fun roundExecuteOnce(registryId: String): Boolean

    /**
     * 刷新战场信息
     */
    fun reloadPlayComboCards()

    /**
     * 回合生命周期处理
     * 问题只能处理一种类型
     */
    fun registerLifecycle(lifecycle: Any)
}


/**
 * todo-future 东西太多,功能也太多了,看后面需不需要部分功能,采用组合
 * todo-future 有空的时候采用委托处理一下
 * select 没有使用私有修饰war,是为了灵活性,没有那个多精力为了安全性去编码,
 */
class MyWarManage(override val war: War) : WarInfo, KoinComponent {

    override var handComboCards = emptyList<ComboCard>()
        private set
    override var playComboCards = emptyList<ComboCard>()
        private set
    override var canUseCards = emptyList<ComboCard>()
        private set
    override val infoMap: Map<String, CardWeightInfo>
    override var extCost: Int = 0
    private val lifecycleRegisterImpl = LifecycleRegisterImpl()

    inline fun consumeExtCost(extCost: Int, consumeCost: (Int) -> Unit) {
        this.extCost = extCost
        try {
            consumeCost(getCost())
        } finally {
            this.extCost = 0
        }
    }
    val toDieHandler = ToDieHandler(this)


    //private val statusReset: StatusReset
    init {
        infoMap = getCardInfos()
        loadKoinModules(module {
            single(named("weightInfo")) { infoMap }
            single { lifecycleRegisterImpl } bind LifecycleRegister::class
        })
        parseCombo(infoMap)
    }

    override fun registerLifecycle(lifecycle: Any) {
        lifecycleRegisterImpl.register(lifecycle)
    }

    //把配置信息转化成上下文信息
    private fun getCardInfos(): Map<String, CardWeightInfo> {
        val infoMap: MutableMap<String, CardWeightInfo> = HashMap()
        ServiceLoaderUtils.loadServices(CardWeightInfoProvide::class.java).forEach {
            infoMap.putAll(it.getInfos())
        }
        return infoMap
    }

    /**
     * 解析Combo信息
     */
    private fun parseCombo(infoMap: Map<String, CardWeightInfo>) {
        val parseCardWeightInfo = getKoin().getAll<ParseCardWeightInfo>()
        //todo-future 插入combo策略,应用不同的排序策略
        parseCardWeightInfo.forEach { it.parse(infoMap) }
    }


    //转化
    fun parseComboCards(cards: List<Card> = getHandCards()): List<ComboCard> {
        return cards.map {
            parseComboCard(it)
        }
    }

    fun parseComboCard(card: Card): ComboCard {
        return ComboCard(
            cardWeightInfo = infoMap[card.cardId],
            card = card
        )
    }

    /**
     * todo-future 使用最后一张的情况处理不了(无法判断是否有变更),会有问题
     */
    inline fun isChange(useCard: () -> ComboCard?): Boolean {
        val beginCard = getHandCards().lastOrNull()
        val expNum = getHandCards().size
        val useResult = useCard() //返回null表示打出失败
        useResult?.let {
            val nowNum = getHandCards().size
            if (nowNum >= expNum) {
                //技能不会被移除的
                return it.card != getPower()
            } else {// 为弃牌写的
                val endCard = getHandCards().lastOrNull()
                if (beginCard != endCard && useResult != beginCard) //排除打出最后一张的情况
                    return true
            }
        }

        return false
    }



    /**
     * todo-future 暂时重新读取数据,性能太差或者有空 改成复杂状态管理
     * 重新加载
     */
    fun reLoad() {
        //select 先转换后再过滤考虑存在费用变更情况
        reLoadHandCards()
        canUseCards = canUseCardsByCost()
        reloadPlayComboCards()
    }

    fun reLoadHandCards() {
        handComboCards = parseComboCards()
    }


    override fun reloadPlayComboCards() {
        playComboCards = parseComboCards(getPlayCards())
    }

    fun cleanWeight() {
        handComboCards.forEach {
            it.cleanWeight()
        }
    }



    /**
     *
     * 节省性能方式,但是对于不是新增在右边会有问题,复杂策略往往来更多bug
     * 需要配合使用
     * [useCardAndRemove]
     * 出问题就用
     * [reLoad]
     * todo-future  看一下comboCards不清空状态会怎么样,看情况决定是否清空状态
     * 没有操作
     */
    fun refreshComboCards() {
        val handCards = getHandCards()
        if (handCards.size > handComboCards.size) {
            val tempList = mutableListOf<ComboCard>()
            for (i in handComboCards.size until handCards.size) {
                val card = handCards[i]
                tempList.add(parseComboCard(card))
            }
            handComboCards += tempList
        }
    }


    /**
     * 过滤出指定费用的卡牌,默认过滤出当前费用
     * @param cost  费用
     */
    fun canUseCardsByCost(cost: Int = getCost()) = handComboCards.filter { comBoCard ->
        comBoCard.card.cost <= cost
    }


    /**
     * 产生复杂的状态,未经测试
     * 操作并改变ComBoCard状态
     *未更新
     */
    fun useCardAndRemove(comBoCard: ComboCard) {

        if (tryUseCard(comBoCard)) {
            handComboCards -= comBoCard
            canUseCards -= comBoCard
            if (comBoCard.card.cardType == CardTypeEnum.MINION) {
                playComboCards += comBoCard
            }

        }
    }

    fun useCard(comboCard: ComboCard): Boolean {
        val card = comboCard.card
        val actionInfo = CARD_INFO_TRIE[card.cardId]
        var result = true
        actionInfo?.let {
            card.action.autoPower(it)
        } ?: run {
            result = comboCard.pointCard?.let {
                card.action.power(comboCard.pointCard)?.let { true } ?: false
            } ?: run {
                card.action.power()?.let {
                    if (card.isChooseOne) {
                        card.action.chooseOne(0)
                    }
                    true
                } ?: false

            }
        }
        result = result && card.area !is HandArea
        return result

    }


    fun tryUseCard(comboCard: ComboCard): Boolean {
        val card = comboCard.card
        //费用不够,动态变更为不能使用
        if (card.cost > getCost() || comboCard.isUnUse()) {
            return false
        }
        //随从已满
        if ((isFull && card.cardType == CardTypeEnum.MINION)) {
            comboCard.unUse()
            return false
        }

        if (card.area !is HandArea) {//区域判断
            if (isPower(card)) {//技能的处理
                if (useSkill) useSkill = false
                else return false
            } else {
                return false
            }

        }
        var useResult = useCard(comboCard)
        if (!useResult && card.area is HandArea) {//再次尝试
            //处理战场已满情况
            if (NotWeight == processPlayCardIsFull()) {
                myLog.info { "再次尝试打出" }
                useResult = useCard(comboCard)
            }
            if (!useResult && !(card.cardType == CardTypeEnum.MINION && isFull)) {
                //处理发现
                /*             if (useStrategyUtils.tryAwait()) {
                                 //补偿发现动画(主要底层原因,无法使用发现),导致无法打出
                                 myLog.info { "发现补偿打出" }
                                 var num = 5
                                 while (useStrategyUtils.tryAwait() && num > 0) {
                                     comboCard.card.action.chooseOne(0)
                                     num--
                                 }
                                 useResult = useCard(comboCard)
                             }*/

                myLog.info { "随机指向打出" }
                useResult = autoPower(card)


            }

        }
        //标记不能打出避免重复尝试
        if (!useResult) comboCard.unUse()
        return useResult
    }



    private var gameId: String? = null

    /**
     * todo debug看一下 判断游戏是否新的一局
     */
    fun isStart(): Boolean {
        val me = war.me
        if (me.resources == 1) {
            var isStart = true
            gameId?.run {
                war.me.gameId
            } ?: {
                if (war.me.gameId == gameId) isStart = false
                war.me.gameId
            }
            return isStart
        }
        return false

    }


    private var isCleanWar = false
    var isFull = false
        private set
    var useSkill = true
        private set
    /**
     * 重新设置状态
     */
    fun reset() {
        isCleanWar = false
        isFull = false
        useSkill = true
        registryInfo.clear()
        //statusReset.reset()
    }
    fun clean() {
        handComboCards = emptyList()
        playComboCards = emptyList()
        canUseCards = emptyList()
        lifecycleRegisterImpl.endRound(this)
    }

    /**
     * 没考虑并发
     */
    override fun cleanPlayByRoundOnce(): Boolean {
        if (!isCleanWar) {
            DeckStrategyUtil.cleanPlay()
            isCleanWar = false
            return true
        }
        return false
    }
    private val registryInfo = HashSet<String>()

    /**
     * 执行过了就返回true
     */
    override fun roundExecuteOnce(registryId: String): Boolean {
        val isExist = registryInfo.contains(registryId)
        if (isExist) {
            return true
        }
        registryInfo.add(registryId)
        return false
    }

    fun processPlayCardIsFull(): Double {
        if (!isFull) {
            isFull = playCardIsFull()
        }
        // 如果战场已满
        if (isFull) {
            // 若已清理过战场则直接返回
            if (isCleanWar) {
                return UnUseWeight
            }
            myLog.info { "随从太多清理一下战场" }

            // 清理战场并更新状态
            cleanPlay()
            isFull = playCardIsFull()
            isCleanWar = true
            if (isFull) return UnUseWeight
        }
        return NotWeight
    }

    /**
     * 生命周期的处理
     */
    private fun lifecycle() {
        lifecycleRegisterImpl.startAllRuleLifecycles(this)
        val isStart = isStart()
        if (isStart) {
            lifecycleRegisterImpl.startAllGameLifecycles()
        }
    }


    /**
     * 策略执行环境
     */
    fun executeEnvironment(runnable: () -> Unit) {
            if (war.isValid()) {
                lifecycle()
                //重新加载信息
                reLoad()
                reset()
                val startNum = getHandCards().size
                //送亡语,送墓场操作
                toDieHandler.processToDie()
                //使用地标
                activeLocation()
                if (startNum > getHandCards().size) reLoad() //重新加载

                runnable()
                //usePower()//使用技能
                myLog.info { "完成所有操作,执行清理战场" }
                //使用地标
                activeLocation()
                //清场
                cleanPlayAll()
                clean()
            } else {
                myLog.warn { "战场无效,不知道为啥会这样" }
            }
    }
}



