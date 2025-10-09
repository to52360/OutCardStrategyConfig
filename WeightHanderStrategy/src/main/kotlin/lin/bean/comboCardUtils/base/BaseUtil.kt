package lin.bean.comboCardUtils.base

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard

fun ComboCard.isMinion() = card.isMinion()
fun Card.isMinion() = cardType == CardTypeEnum.MINION
fun ComboCard.atc() = card.atc