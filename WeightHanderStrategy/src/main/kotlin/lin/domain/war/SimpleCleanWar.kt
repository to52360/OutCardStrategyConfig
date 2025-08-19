package lin.domain.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.Player
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard

class SimpleCleanWar(val canAttacks: MutableList<ComboCard>, val rival: Player) {
    val rivalPlayArea = rival.playArea


    lateinit var auraCards: MutableList<Card>
    lateinit var tauntCards: MutableList<Card>
    lateinit var rivalCards: MutableList<Card>

    /**
     * 判断有没有攻击目标,有进行初始化
     */
    private fun initAttack(): Boolean {
        if (rivalPlayArea.cards.isEmpty()) return false
        rivalCards = rivalPlayArea.cards.toMutableList()//复制一下避免并发修改错误
        auraCards = mutableListOf()
        tauntCards = mutableListOf()
        val iterator = rivalCards.iterator()
        while (iterator.hasNext()) {
            val card = iterator.next()
            if (card.isTaunt) {
                tauntCards.add(card)
                iterator.remove()
            } else if (card.isAura) {
                auraCards.add(card)
                iterator.remove()
            } else if (card.cardType != CardTypeEnum.MINION) {
                iterator.remove()
            }

        }
        return true
    }

    fun executeAttack() {
        if (initAttack())
            attack()
    }

    private fun attack() {
        if (processAttack(canAttacks, tauntCards)) {
            if (processHero()) {
                if (processAttack(canAttacks, auraCards)) {//处理光环
                    processAttack(canAttacks, rivalCards)
                }
            }
        }
    }

    private fun processHero(): Boolean {
        val sumAtc = canAttacks.sumOf { it.card.atc }
        val rivalPlayArea = rival.playArea
        val rivalHero = rivalPlayArea.hero ?: return true

        val rivalHeroBlood = rivalHero.blood()
        if (sumAtc >= rivalHeroBlood) {
            processAttack(canAttacks, mutableListOf(rivalHero))
            return false
        }
        return true
    }

    fun processAttack(canAttacks: MutableList<ComboCard>, targetCards: MutableList<Card>): Boolean {
        if (targetCards.isEmpty()) return true
        for (targetCard: Card in targetCards) {
            if (canAttacks.isEmpty()) return false  //没有可攻击直接返回
            val iterator = canAttacks.iterator()
            while (iterator.hasNext() && !targetCard.isDead()) {//死亡换下一个对象
                val card = iterator.next()
                card.card.action.attack(targetCard)//攻击
                iterator.remove()//只能攻击一次
            }
        }
        return canAttacks.isNotEmpty()
    }

}