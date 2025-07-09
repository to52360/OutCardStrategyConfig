package lin

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

import club.xiaojiawei.config.log
import club.xiaojiawei.data.CARD_WEIGHT_TRIE

import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.strategy.HsCommonDeckStrategy
import club.xiaojiawei.strategy.HsRadicalDeckStrategy

import lin.dao.ComboDao
import lin.dao.defaultOutCardLambda


/**
 * @see club.xiaojiawei.bean.BaseCard
 * @see club.xiaojiawei.bean.Player
 * 插件管理
 *
 * 参考[HsRadicalDeckStrategy]
 * 权重表[CARD_WEIGHT_TRIE]
 */
class WeightHandlerStrategy : DeckStrategy() {
    private var deckStrategy: () -> Unit


    init {
         val comboDao  = ComboDao(CARD_WEIGHT_TRIE.data(), WAR)
        deckStrategy = try {
            comboDao.getOutCardLambda()
        }catch (e: Exception){
            //todo-future 方便调试,以后删除
            e.printStackTrace()
            defaultOutCardLambda
        }

    }

    private val commonDeckStrategy = HsCommonDeckStrategy()

    override fun name(): String = "权重处理策略"

    override fun description(): String = "会在基础策略的基础上使用战吼，法术，地标牌（依旧不识别战吼或法术）"

    override fun getRunMode(): Array<RunModeEnum> =
        arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)

    override fun deckCode(): String = ""

    override fun id(): String = "e71234fa-1-radical-deck-97e9-1f4e126cd33b"

    override fun referWeight(): Boolean = true

    override fun referPowerWeight(): Boolean = true

    override fun referChangeWeight(): Boolean = true

    override fun executeChangeCard(cards: HashSet<Card>) {
        commonDeckStrategy.executeChangeCard(cards)
    }



    override fun executeOutCard() {
        try{
            deckStrategy()
        }catch(e:Exception){
            //todo-future 方便调试,以后删除
            e.printStackTrace()
            log.error(e) { "Failed to execute card. 异常信息:"+e.localizedMessage }
            if(deckStrategy == defaultOutCardLambda) throw e //激进策略的问题
            else{
                deckStrategy = defaultOutCardLambda
                deckStrategy()
            }

        }

    }

    /**
     * todo 发现策略选择
     */

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = commonDeckStrategy.executeDiscoverChooseCard(*cards)
}