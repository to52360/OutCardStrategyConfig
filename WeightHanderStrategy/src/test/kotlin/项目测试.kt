import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.TEST_CARD_ACTION
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum

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