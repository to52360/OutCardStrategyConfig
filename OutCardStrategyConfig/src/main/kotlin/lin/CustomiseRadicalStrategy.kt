package lin

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import club.xiaojiawei.data.CARD_WEIGHT_TRIE

import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.strategy.HsCommonDeckStrategy
import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import lin.dao.ComboWeightGroup
import lin.dao.defaultOutCardLambda


/**
 * @see club.xiaojiawei.bean.BaseCard
 * @see club.xiaojiawei.bean.Player
 * 插件管理
 *
 * 参考[HsRadicalDeckStrategy]
 * 权重表[CARD_WEIGHT_TRIE]
 */
class CustomiseRadicalStrategy : DeckStrategy() {
    private var deckStrategy: (War) -> Unit


    init {
         val comboWeightGroup  = ComboWeightGroup(CARD_WEIGHT_TRIE.data())
         deckStrategy = comboWeightGroup.getOutCardLambda()
    }

    private val commonDeckStrategy = HsCommonDeckStrategy()

    override fun name(): String = "激进成就策略"

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

    /**
     * Attempts to play a card, handles resource deduction and logging.
     * Assumes card.action.power() will handle moving the card between areas.
     */
    private fun tryPlayCard(cardToPlay: Card, target: Card? = null, player: Player): Boolean {
        if (cardToPlay.cost > player.usableResource) {
            log.debug { "Cannot play ${cardToPlay.cardId} (cost ${cardToPlay.cost}): not enough usableResource (${player.usableResource})." }
            return false
        }

        val playSuccess: Boolean
        if (target != null) {
            log.info { "Player ${player.playerId} attempting to play ${cardToPlay.cardId} (cost ${cardToPlay.cost}) targeting ${target.cardId}." }
            playSuccess = cardToPlay.action.power(target)!=null
        } else {
            log.info { "Player ${player.playerId} attempting to play ${cardToPlay.cardId} (cost ${cardToPlay.cost}) without target." }
            playSuccess = cardToPlay.action.power()!=null
        }

        if (playSuccess) {
            log.info { "Successfully played ${cardToPlay.cardId}. Remaining usableResource: ${player.usableResource}." }
            // Card movement (e.g., hand to play, spell to graveyard) is assumed to be handled by card.action.power()
            // If not, manual updates to player.handArea.cards.remove(cardToPlay), etc., would be needed here.
            return true
        } else {
            log.warn { "Failed to play ${cardToPlay.cardId}." }
            return false
        }
    }


    override fun executeOutCard() {
        try{
            deckStrategy(WAR)
        }catch(e:Exception){
            log.error(e) { "Failed to execute card. 异常信息:"+e.localizedMessage }
            if(deckStrategy == defaultOutCardLambda) throw e
            else{
                deckStrategy = defaultOutCardLambda
                deckStrategy(WAR)
            }

        }

    }
    private var gameId :String? = null
    fun isStart() : Boolean{
        val me = WAR.me
        if(me.resources==0||me.resources==1){
            log.info { "resources属性值为"+me.resources }
            var isStart = true
            gameId?.run{
                WAR.me.gameId
            }?:{
                if(WAR.me.gameId == gameId) isStart =false
                WAR.me.gameId
            }
            return isStart
        }
        return false

    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = commonDeckStrategy.executeDiscoverChooseCard(*cards)
}