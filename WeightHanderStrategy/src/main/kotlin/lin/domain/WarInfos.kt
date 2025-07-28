package lin.domain

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.enums.CardTypeEnum

/**
 * ai辅助生成
 */
object WarInfos {
// ... existing code ...

    /**
     * 敌方场上随从攻击力总和
     */
    fun MyWarManage.findRivalAtcSum(): Int {
        return war.rival.playArea.cards.sumOf { it.atc }
    }

    /**
     * 我方场上随从攻击力总和
     */
    fun MyWarManage.findMeAtcSum(): Int {
        return war.me.playArea.cards.sumOf { it.atc }
    }

    /**
     * 敌方攻击最高的随从
     */
    fun MyWarManage.findRivalMaxAttackMinion(): Card? {
        return war.rival.playArea.cards
            .filter { it.cardType == CardTypeEnum.MINION }
            .maxByOrNull { it.atc }
    }

    fun MyWarManage.rivalAllCardsByPlayArea() = war.rival.playArea.cards.toList()

    fun MyWarManage.rivalSecretCount() = war.rival.secretArea.cards.size

    fun MyWarManage.rivalHandSize() = war.rival.handArea.cards.size

    /**
     * 我方攻击最高的随从
     */
    fun MyWarManage.findMeMaxAttackMinion(): Card? {
        return war.me.playArea.cards
            .filter { it.cardType == CardTypeEnum.MINION }
            .maxByOrNull { it.atc }
    }

    /**
     * 敌方生命值最少的卡牌
     */
    fun MyWarManage.findRivalMinHealthCard(): Card? {
        return war.rival.playArea.cards.minByOrNull { it.health }
    }

    /**
     * 我方生命值最少的卡牌
     */
    fun MyWarManage.findMeMinHealthCard(): Card? {
        return war.me.playArea.cards.minByOrNull { it.health }
    }

    /**
     * 查找敌方指定攻击力的卡牌
     */
    fun MyWarManage.findRivalCardsByAttack(attack: Int): List<Card> {
        return war.rival.playArea.cards.filter { it.atc == attack }
    }

    /**
     * 查找我方指定攻击力的卡牌
     */
    fun MyWarManage.findMeCardsByAttack(attack: Int): List<Card> {
        return war.me.playArea.cards.filter { it.atc == attack }
    }

    /**
     * 查找敌方指定生命值的卡牌
     */
    fun MyWarManage.findRivalCardsByHealth(health: Int): List<Card> {
        return war.rival.playArea.cards.filter { it.health == health }
    }

    /**
     * 查找我方指定生命值的卡牌
     */
    fun MyWarManage.findMeCardsByHealth(health: Int): List<Card> {
        return war.me.playArea.cards.filter { it.health == health }
    }

    /**
     * 自定义查询敌方卡牌
     */
    fun MyWarManage.findRivalCardsByFilter(filter: (Card) -> Boolean): List<Card> {
        return war.rival.playArea.cards.filter { filter(it) }
    }

    /**
     * 自定义查询我方卡牌
     */
    fun MyWarManage.findMeCardsByFilter(filter: (Card) -> Boolean): List<Card> {
        return war.me.playArea.cards.filter { filter(it) }
    }
    fun MyWarManage.getGraveyardCards()=war.me.graveyardArea.cards

// ... existing code ...
}