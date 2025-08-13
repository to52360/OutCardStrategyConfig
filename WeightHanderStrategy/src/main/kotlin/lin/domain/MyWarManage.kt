package lin.domain

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.bean.area.HandArea
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.data.CARD_INFO_TRIE
import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.myLog
import lin.serviceLoader.cardInfoProvide.CardWeightInfoProvide
import lin.serviceLoader.parse.ParseCardWeightInfo
import lin.serviceLoader.weightRule.CardRule
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.action.activeLocation
import lin.warExt.action.cleanPlay
import lin.warExt.action.usePower
import lin.warExt.base.getHandCards
import lin.warExt.base.getNowCost
import lin.warExt.base.getPlayCards
import org.koin.core.component.KoinComponent


interface WarInfo {
    val war: War
    val handComboCards: List<ComboCard>
    val canUseCards: List<ComboCard>
    val playComboCards: List<ComboCard>
    val infoMap: Map<String, CardWeightInfo>
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


    init {
        infoMap = getCardInfos()
        parseCondition(infoMap)
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
        parseCardWeightInfo.forEach { it.parse(infoMap) }
    }
    private fun parseCondition(infoMap: Map<String, CardWeightInfo>) {

        ServiceLoaderUtils.loadServices(CardRule::class.java).forEach {
            val card = infoMap[it.cardId()]
            card?.run {
                addWeightRule(it)
            }
        }
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
     * select 暂时重新读取数据,性能太差或者有空 改成如果一直如不用更改
     * 重新加载
     */
    fun reLoad() {
        //select 先转换后再过滤考虑存在费用变更情况
        handComboCards = parseComboCards()
        canUseCards = canUseCardsByCost()
        reloadPlayComboCards()
    }
    var executeCleanWar = false


    private fun reloadPlayComboCards() {
        playComboCards = parseComboCards(getPlayCards())
    }

    fun cleanWeight() {
        handComboCards.forEach {
            it.cleanWeight()
        }
    }

    /**
     * todo-future 有问题使用需要特定组合,但是没有绑定在一起
     */
    private fun isChange(): Boolean {
        val change = getHandCards().size >= handNum
        return change
    }

    fun changeAndReload(): Boolean {
        val change = isChange();
        if (change) {
            reLoad()
        }
        return change
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
    fun canUseCardsByCost(cost: Int = getNowCost()) = handComboCards.filter { comBoCard ->
        comBoCard.card.cost <= cost
    }


    /**
     * 操作并改变ComBoCard状态
     *
     */
    fun useCardAndRemove(comBoCard: ComboCard) {
        if (useCard(comBoCard)) {
            handComboCards -= comBoCard
        }
    }
    private var handNum = 0

    //todo-future 不一定能使用出去  打不出去尝试指向关联组 ,该方法好像也不符合战场范畴
    //todo 可以判断最后一个下标等不等于最后下标
    fun useCard(comBoCard: ComboCard): Boolean {
        handNum = getHandCards().size
        val card = comBoCard.card
        if (card.area !is HandArea) return false //修改区域


        val actionInfo = CARD_INFO_TRIE[card.cardId]
        var result = true
        actionInfo?.let {
            card.action.autoPower(it)
        } ?: run {
            result = comBoCard.pointCard?.let {
                card.action.power(comBoCard.pointCard)?.let { true } ?: false
            } ?: run {
                card.action.power()?.let { true } ?: false
            }
        }
        return result && card.area !is HandArea

    }
    //todo-future 不知道并发安全不,执行出牌策略和更新war是不是同一个线程


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

    //有费用


    /**
     * 策略执行环境
     * todo-future 可见性原因,不支持内联
     */
    inline fun executeEnvironment(runnable: () -> Unit) {
            if (war.isValid()) {
                //使用地标
                activeLocation()
                //重新加载信息
                reLoad()

                runnable()
                usePower()//使用技能
                activeLocation()
                //清场
                cleanPlay()
            } else {
                myLog.warn { "战场无效,不知道为啥会这样" }
            }


    }
}