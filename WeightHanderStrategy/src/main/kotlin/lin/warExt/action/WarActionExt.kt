package lin.warExt.action

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.util.DeckStrategyUtil
import lin.domain.MyWarManage

/**
 * 怕战场管理太多代码,功能性代码移到这里
 */

/**
 * 使用地标
 */
fun MyWarManage.activeLocation(){
    val cards = war.me.playArea.cards
    cards.forEach { card ->
        if (card.cardType === CardTypeEnum.LOCATION && !card.isLocationActionCooldown) {
            card.action.lClick()
            Thread.sleep(3000)
        }
    }
}

/**
 * 清场
 */
fun MyWarManage.cleanPlay() {
    DeckStrategyUtil.cleanPlay()
}

/**
 * 发射星剑
 */
fun MyWarManage.useLaunch(){
    val me = war.me
    me.playArea.cards.toList().forEach { card: Card ->
        if (card.isLaunchpad && me.usableResource >= card.launchCost()) {
            card.action.launch()
        }
    }
}
/**
 * 使用技能
 * [club.xiaojiawei.strategy.HsCommonDeckStrategy.executeOutCard]
 */
fun MyWarManage.usePower(){
    val me = war.me
//        使用技能
    me.playArea.power?.let {
        if (me.usableResource >= it.cost || it.cost == 0) {
            it.action.power()
        }
    }
}
