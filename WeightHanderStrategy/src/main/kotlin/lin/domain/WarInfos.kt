package lin.domain

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.enums.CardTypeEnum

/**
 * ai辅助生成
 */
class WarInfos(private val war: War) {
    // 敌方场上随从攻击力总和
    fun findRivalAtcSum(): Int {
        return war.rival.playArea.cards.sumOf { it.atc }
    }

    // 我方场上随从攻击力总和
    fun findMeAtcSum(): Int {
        return war.me.playArea.cards.sumOf { it.atc }
    }

    // 敌方攻击最高的随从
    fun findRivalMaxAttackMinion(): Card? {
        return war.rival.playArea.cards
            .filter { it.cardType == CardTypeEnum.MINION }
            .maxByOrNull { it.atc }
    }
    //
    fun rivalAllCardsByPlayArea()=war.rival.playArea.cards.toList()

    fun rivalSecretCount()=war.rival.secretArea.cards.size

    fun rivalHandSize() = war.rival.handArea.cards.size

    // 我方攻击最高的随从
    fun findMeMaxAttackMinion(): Card? {
        return war.me.playArea.cards
            .filter { it.cardType == CardTypeEnum.MINION }
            .maxByOrNull { it.atc }
    }

    // 敌方生命值最少的卡牌
    fun findRivalMinHealthCard(): Card? {
        return war.rival.playArea.cards.minByOrNull { it.health }
    }

    // 我方生命值最少的卡牌
    fun findMeMinHealthCard(): Card? {
        return war.me.playArea.cards.minByOrNull { it.health }
    }

    // 查找敌方指定攻击力的卡牌
    fun findRivalCardsByAttack(attack: Int): List<Card> {
        return war.rival.playArea.cards.filter { it.atc == attack }
    }

    // 查找我方指定攻击力的卡牌
    fun findMeCardsByAttack(attack: Int): List<Card> {
        return war.me.playArea.cards.filter { it.atc == attack }
    }

    // 查找敌方指定生命值的卡牌
    fun findRivalCardsByHealth(health: Int): List<Card> {
        return war.rival.playArea.cards.filter { it.health == health }
    }

    // 查找我方指定生命值的卡牌
    fun findMeCardsByHealth(health: Int): List<Card> {
        return war.me.playArea.cards.filter { it.health == health }
    }

    // 自定义查询
     fun findRivalCardsByFilter(filter:(Card)->Boolean ): List<Card> {
        return war.rival.playArea.cards.filter { filter(it) }
    }

    // 查找我方同时指定攻击和生命值的卡牌
    fun findMeCardsByFilter(filter:(Card)->Boolean): List<Card> {
        return war.me.playArea.cards.filter { filter(it) }
    }
}