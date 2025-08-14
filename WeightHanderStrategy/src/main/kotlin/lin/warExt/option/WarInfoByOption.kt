package lin.warExt.option

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.domain.WarInfo

/**
 * 我方生命值最少的卡牌
 */
fun WarInfo.findMeMinHealthCard(): Card? {
    return war.me.playArea.cards.minByOrNull { it.health }
}

/**
 * 查找敌方指定攻击力的卡牌
 */
fun WarInfo.findRivalCardsByAttack(attack: Int): List<Card> {
    return war.rival.playArea.cards.filter { it.atc == attack }
}

/**
 * 查找我方指定攻击力的卡牌
 */
fun WarInfo.findMeCardsByAttack(attack: Int): List<Card> {
    return war.me.playArea.cards.filter { it.atc == attack }
}

/**
 * 查找敌方指定生命值的卡牌
 */
fun WarInfo.findRivalCardsByHealth(health: Int): List<Card> {
    return war.rival.playArea.cards.filter { it.health == health }
}

/**
 * 查找我方指定生命值的卡牌
 */
fun WarInfo.findMeCardsByHealth(health: Int): List<Card> {
    return war.me.playArea.cards.filter { it.health == health }
}

/**
 * 自定义查询敌方卡牌
 */
fun WarInfo.findRivalCardsByFilter(filter: (Card) -> Boolean): List<Card> {
    return war.rival.playArea.cards.filter { filter(it) }
}

/**
 * 自定义查询我方卡牌
 */
fun WarInfo.findMeCardsByFilter(filter: (Card) -> Boolean): List<Card> {
    return war.me.playArea.cards.filter { filter(it) }
}