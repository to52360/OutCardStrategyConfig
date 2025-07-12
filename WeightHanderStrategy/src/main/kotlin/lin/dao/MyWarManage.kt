package lin.dao

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.bean.isValid

import club.xiaojiawei.data.CARD_INFO_TRIE
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.DeckStrategyUtil
import lin.bean.ComboCard
import lin.myLog
import lin.weightHandler.condition.bean.ComboWeightInfo

/**
 * 可见性语义,没有接口语义
 */
interface MyWarInfo{
    /**
     * 获取能够使用的牌
     */
   fun getCanUseCardsByCost():List<ComboCard>

    /**
     * 获取全部手牌
     */
   fun getHandComboCards():List<ComboCard>

    /**
     * 获取墓场的牌
     */
   fun getGraveyardCards():List<Card>

    /**
     * 可以费用
     */
   fun getNowCost():Int

    /**
     * 最新的手牌数据
     */
    fun getHandCards():List<Card>
}




/**
 * todo-future 东西太多,功能也太多了,看后面需不需要部分功能,采用组合
 * todo-future 还差全局战场
 * select 没有使用私有修饰war,是为了灵活性,没有那个多精力为了安全性去编码,
 */
class MyWarManage(val war:War, private val infoMap : Map<String, ComboWeightInfo> ) : MyWarInfo {
    private var comboCards  = emptyList<ComboCard>()
    private var canUseCards = emptyList<ComboCard>()


    fun isValid()=war.isValid()






    //转化
    private fun parseComboCard() {
         getHandCards().map { card ->
            ComboCard(
                comboWeightInfo = infoMap[card.cardId],
                card = card
            )
        }.also {
            comboCards = it
        }
    }

    override fun getHandCards(): List<Card> {
        return war.me.handArea.cards
    }

    override fun getHandComboCards()=comboCards
    override fun getGraveyardCards()=war.me.graveyardArea.cards

    /**
     * select 暂时重新读取数据,性能太差或者有空 改成如果一直如不用更改
     * 重新加载
     */
     fun reLoad(){
         //select 先转换后再过滤考虑存在费用变更情况
         parseComboCard()
        canUseCards = canUseCards()
    }

    /**
     *
     * 节省性能方式,但是对于不是新增在右边会有问题,复杂策略往往来更多bug
     * 需要配合使用
     * [useCardAndUpdate]
     * 出问题就用
     * [reLoad]
     * todo-future  看一下comboCards不清空状态会怎么样,看情况决定是否清空状态
     */
    fun refreshComboCards(){
        val handCards = getHandCards()
        if (handCards.size > comboCards.size) {
            val tempList = mutableListOf<ComboCard>()
            for (i in comboCards.size until handCards.size) {
                val card = handCards[i]
                tempList.add(ComboCard(infoMap[card.cardId], card))
            }
            comboCards += tempList
        }
    }
    override fun getCanUseCardsByCost()= canUseCards

    /**
     * 过滤出指定费用的卡牌,默认过滤出当前费用
     * @param cost  费用
     */
     fun canUseCards(cost:Int = getNowCost())=comboCards.filter {
            comBoCard ->  comBoCard.card.cost<= cost
    }
    fun useCardAndUpdate(comBoCard: ComboCard){
        if(useCard(comBoCard)){
            comboCards-=comBoCard
        }
    }
    //todo-future 不一定能使用出去  打不出去尝试指向关联组 ,该方法好像也不符合战场范畴
     fun useCard(comBoCard: ComboCard):Boolean{
        val card = comBoCard.card
        val actionInfo = CARD_INFO_TRIE[card.cardId]
        var result = true
        actionInfo?.let {
            card.action.autoPower(it)
        }?:run{
            result = card.action.power()?.let { true }?:false
        }
        return result

    }
    //todo-future 不知道并发安全不,执行出牌策略和更新war是不是同一个线程
    override fun getNowCost()= war.me.usableResource

    /**
     * 费用没打完,考虑使用为负数权重的牌
     */
    fun costNotFillAllCard(): Boolean {
        TODO("Not yet implemented")
    }
    private var gameId :String? = null

    /**
     * 判断游戏是否新的一局
     */
    fun isStart() : Boolean{
        val me = war.me
        if(me.resources==0||me.resources==1){
            //todo-future 看一下是0还是1
            myLog.info { "resources属性值为"+me.resources }
            var isStart = true
            gameId?.run{
                war.me.gameId
            }?:{
                if(war.me.gameId == gameId) isStart =false
                war.me.gameId
            }
            return isStart
        }
        return false

    }

    //有费用
    fun hasCost()=getNowCost()>0

    /**
     * 策略执行环境
     */
    inline fun executeEnvironment(runnable: () -> Unit) {
        try {
            if (isValid()) {
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

        } catch (e: Exception) {
            e.printStackTrace()
            myLog.error(e) { "执行出牌逻辑出错" }
            throw e
        }
    }
}