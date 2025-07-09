package lin.dao

import club.xiaojiawei.enums.CardTypeEnum

/**
 * 使用地标
 */
fun MyWarManage.activeLocation(){
    val cards = war.me.playArea.cards
    cards.forEach { card ->
        if (card.cardType === CardTypeEnum.LOCATION && !card.isLocationActionCooldown) {
            card.action.lClick()
        }
    }
}