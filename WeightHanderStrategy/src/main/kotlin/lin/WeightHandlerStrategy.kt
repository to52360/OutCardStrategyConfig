package lin

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card


import club.xiaojiawei.data.BaseData
import club.xiaojiawei.data.CARD_WEIGHT_TRIE

import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR

import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import club.xiaojiawei.util.DeckStrategyUtil

import lin.dao.ComboDao
import lin.dao.defaultOutCardLambda


/**
 * @see club.xiaojiawei.bean.BaseCard
 * @see club.xiaojiawei.bean.Player
 * 插件管理
 * 参考[HsRadicalDeckStrategy]
 * 权重表[CARD_WEIGHT_TRIE]
 * WeightHandlerPlugin
 */
class WeightHandlerStrategy : DeckStrategy() {
    private var deckStrategy: () -> Unit


    init {
        myLog.info { "插件初始化" }
        deckStrategy = try {
            val comboDao  = ComboDao(CARD_WEIGHT_TRIE.data(), WAR)
            comboDao.getOutCardLambda()
        }catch (e: Exception){
            myLog.error(e) { e.message }
            defaultOutCardLambda
        }


    }


    override fun name(): String = "权重处理策略"

    override fun description(): String = "基于战场计算权重的策略,例如在手牌对应种族就加权重,通过配置绑定到组,然后通过组id关联到权重表(CardWeight)的weight,依赖数据也是\n"

    override fun getRunMode(): Array<RunModeEnum> =
        arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)

    override fun deckCode(): String = ""

    override fun id(): String = "e71234fa-1-weightHandler-deck-97e9-1f4e126cd33b"

    override fun referWeight(): Boolean = true

    override fun referPowerWeight(): Boolean = true

    override fun referChangeWeight(): Boolean = true

    override fun executeChangeCard(cards: HashSet<Card>) {
        myLog.info { "插件执行换牌策略" }
        if (BaseData.enableChangeWeight) {
            val weightCards = DeckStrategyUtil.convertToSimulateCard(cards.toList())
            weightCards.sortByDescending { it.changeWeight }
            for (card in weightCards) {
                if (card.changeWeight < 0.0) {
                    cards.remove(card.card)
                }
            }
        } else {
            cards.removeIf { card -> card.cost > 2 }
        }
    }



    override fun executeOutCard() {
        myLog.info { "插件执行出牌策略 策略类名:${deckStrategy}" }
        try{
            deckStrategy()
        }catch(e:Exception){
            myLog.error(e) { "Failed to execute card. 异常信息:"+e.localizedMessage }
            if(deckStrategy == defaultOutCardLambda) throw e //激进策略的问题
            else{
                deckStrategy = defaultOutCardLambda
                deckStrategy()
            }

        }

    }

    /**
     * todo-future 发现策略选择
     */

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = 1
}