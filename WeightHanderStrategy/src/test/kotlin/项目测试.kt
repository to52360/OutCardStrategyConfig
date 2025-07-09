import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.TEST_CARD_ACTION
import club.xiaojiawei.enums.CardTypeEnum

class 项目测试 {
}
val myCards = mutableListOf(
    Card(TEST_CARD_ACTION).apply {
        cardType = CardTypeEnum.MINION
        entityId = "m1"
        isPoisonous = true
        atc = 1
        health = 3
    },
    Card(TEST_CARD_ACTION).apply {
        cardType = CardTypeEnum.MINION
        entityId = "m2"
        atc = 3
        health = 3
    },
    Card(TEST_CARD_ACTION).apply {
        cardType = CardTypeEnum.HERO
        entityId = "mh1"
        atc = 0
        health = 30
    },
)