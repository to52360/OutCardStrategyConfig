package lin

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card


import club.xiaojiawei.config.log
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
 *[CardId]
 * 参考[HsRadicalDeckStrategy]
 * 权重表[CARD_WEIGHT_TRIE]
 * WeightHandlerPlugin
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
     * todo-future 发现策略选择
     */

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = 1
}