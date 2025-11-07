package lin.extWeightHandler

import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.weightHandler.warHandler.WarHandler

class ToDieWarHandler : WarHandler {
    override fun isToDie(comboCard: ComboCard, myWarManage: MyWarManage): Boolean {
        val card = comboCard.card
        if (card.isDeathRattle && !card.isPoisonous) {//没有适配,剧毒不参与
            return card.cost * 2 > card.atc + card.health
        }
        return false
    }
}