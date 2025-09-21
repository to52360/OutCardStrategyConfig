package lin.domain


import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.War
import club.xiaojiawei.hsscriptcardsdk.bean.area.HandArea
import club.xiaojiawei.hsscriptcardsdk.bean.isValid
import club.xiaojiawei.hsscriptcardsdk.data.CARD_INFO_TRIE
import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.strategy.UseStrategyUtils
import lin.myLog
import lin.serviceLoader.cardInfoProvide.CardWeightInfoProvide
import lin.serviceLoader.parse.ParseCardWeightInfo
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.action.activeLocation
import lin.warExt.action.cleanPlay
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getHandCards
import lin.warExt.my.base.getPlayCards
import lin.warExt.my.base.playCardIsFull
import lin.warExt.rival.rivalBlood
import lin.weightHandler.warHandler.ToDieHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


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
    private val useStrategyUtils = get<UseStrategyUtils>()

    inline fun consumeExtCost(extCost: Int, consumeCost: (Int) -> Unit) {
        this.extCost = extCost
        try {
            consumeCost(getCost())
        } finally {
            this.extCost = 0
        }
    }
    val toDieHandler = ToDieHandler(this)



    init {
        infoMap = getCardInfos()
        parseCombo(infoMap)
    }

    //把配置信息转化成上下文信息
    private fun getCardInfos(): Map<String, CardWeightInfo> {
        var infoMap: Map<String, CardWeightInfo> = emptyMap()
        ServiceLoaderUtils.loadServices(CardWeightInfoProvide::class.java).forEach {
            infoMap = it.getInfos() + infoMap
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
        val useCard = useCard() //返回null表示打出失败
        useCard?.let {
            val nowNum = getHandCards().size
            if (nowNum >= expNum) {
                return true
            } else {// 为弃牌写的
                val endCard = getHandCards().lastOrNull()
                if (beginCard != endCard && useCard != beginCard) //排除打出最后一张的情况
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
        handComboCards = parseComboCards()
        canUseCards = canUseCardsByCost()
        reloadPlayComboCards()
    }


    fun reloadPlayComboCards() {
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
            playComboCards += comBoCard
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
                card.action.power()?.let { true } ?: false
            }
        }
        result = result && card.area !is HandArea
        return result

    }


    fun tryUseCard(comboCard: ComboCard): Boolean {
        val card = comboCard.card
        //费用不够
        if (card.cost > getCost()) {
            return false
        }

        if (card.area !is HandArea) {//区域判断
            if (war.me.playArea.power != comboCard.card) //技能的处理
                return false
        }
        var useResult = useCard(comboCard)
        if (!useResult && card.area is HandArea) {//再次尝试

            //处理发现
            if (useStrategyUtils.tryAwait()) {
                //补偿发现动画,导致无法打出
                useResult = useCard(comboCard)
            }


            //处理战场已满情况
            if (!useResult && NotWeight == processPlayCardIsFull()) {
                myLog.info { "再次尝试打出" }
                useResult = useCard(comboCard)
            }

        }
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
    private var isFull = false

    /**
     * 重新设置状态
     */
    fun reset() {
        isCleanWar = false
        isFull = false
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
     * 策略执行环境
     */
    inline fun executeEnvironment(runnable: () -> Unit) {
            if (war.isValid()) {
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
                //activeLocation()
                myLog.info { "完成所有操作,执行清理战场" }
                //使用地标
                activeLocation()
                //清场
                cleanPlay()
            } else {
                myLog.warn { "战场无效,不知道为啥会这样" }
            }
    }
}

private fun List<ComboCard>.isNotExecuteToDie(war: WarInfo): Boolean {
    val sumAtc = sumOf { comboCard -> comboCard.card.atc }

    return war.rivalBlood() - sumAtc < 10
}


