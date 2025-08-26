package lin.domain.war

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.Player
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import lin.bean.ComboCard
import lin.myLog
import java.util.*

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
        rivalCards = LinkedList(rivalPlayArea.cards)//复制一下避免并发修改错误
        auraCards = mutableListOf()
        tauntCards = mutableListOf()
        val iterator = rivalCards.iterator()
        while (iterator.hasNext()) {
            val card = iterator.next()
            if (card.isTaunt) {
                tauntCards.add(card)
                iterator.remove()
            } else if (card.isAura || card.isTriggerVisual) {
                auraCards.add(card)
                iterator.remove()
            } else if (card.cardType != CardTypeEnum.MINION) {
                iterator.remove()
            }

        }
        return true
    }

    fun executeAttack() {
        if (initAttack()) {
            myLog.info { "执行送随从" }
            attack()
        }

    }

    private fun attack() {
        if (processAttack(tauntCards)) return
        if (processHero()) return
        if (processAttack(auraCards)) return //处理嘲讽
        processAttack(rivalCards) //处理光环
    }

    private fun processHero(): Boolean {
        val sumAtc = canAttacks.sumOf { it.card.atc }
        val rivalPlayArea = rival.playArea
        val rivalHero = rivalPlayArea.hero ?: return false

        val rivalHeroBlood = rivalHero.blood()
        if (sumAtc >= rivalHeroBlood) {
            processAttack(mutableListOf(rivalHero))
            return true
        }
        return false
    }

    /**
     * @return 没有可攻击 返回true
     */
    private fun processAttack(targetCards: MutableList<Card>): Boolean {
        when (targetCards.size) {
            0 -> return false
            1 -> return attackTarget(targetCards.first())
            else -> {
                val targetCardsOrder = buildAttackOrder(targetCards)
                for (targetCard: Card in targetCardsOrder) {
                    if (attackTarget(targetCard)) return true  //没有可攻击直接返回
                }
                return canAttacks.isEmpty()
            }
        }
    }

    /**
     * 选择攻击开始位置
     * chatgpt生成的算法
     */
    private fun buildAttackOrder(targetCards: MutableList<Card>): List<Card> {
        val atcSum = canAttacks.sumOf { it.card.atc }

        // 1) 全局按血量降序，再按id升序
        targetCards.sortWith(compareByDescending<Card> { it.blood() }.thenBy { it.entityId })

        val n = targetCards.size
        // 2) 找到第一张可击杀卡的位置（即“尾段”的起点）
        val firstKillable = targetCards.indexOfFirst { it.blood() <= atcSum }
        val k = if (firstKillable == -1) n else firstKillable
        val tailLen = n - k // 可击杀段长度

        when (tailLen) {
            0 -> { // 没有可击杀：整体升序
                targetCards.reverse()
            }

            n -> {
                // 全部可击杀：保持当前（降序）即可
            }

            else -> {
                // 3) 旋转：把尾段(可击杀)前移
                java.util.Collections.rotate(targetCards, tailLen)
                // 4) 把现在尾部的“不可击杀段”反转为升序
                targetCards.subList(tailLen, n).reverse()
            }
        }
        return targetCards

    }

    /**
     * @return  可用攻击为null返回true
     */
    private fun attackTarget(targetCard: Card): Boolean {
        if (canAttacks.isEmpty()) return true  //没有可攻击直接返回
        val iterator = canAttacks.iterator()
        while (iterator.hasNext() && !targetCard.isDead()) {//死亡换下一个对象
            val card = iterator.next()
            card.card.action.attack(targetCard)//攻击
            iterator.remove()//只能攻击一次
        }
        return canAttacks.isEmpty()
    }

}