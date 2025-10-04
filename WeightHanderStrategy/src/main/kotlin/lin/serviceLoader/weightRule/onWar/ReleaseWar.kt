package lin.serviceLoader.weightRule.onWar

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.util.CardUtil
import lin.bean.CleanWarId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.strategy.UseBeforeStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.lifecycle.RoundLifecycle
import lin.serviceLoader.weightRule.onWar.ReleaseWar.Companion.ALL_CLEAN
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.cardUtils.isExtWeight
import lin.warExt.my.base.meBlood
import lin.warExt.my.twoLambda.findAtcSum
import lin.warExt.my.twoLambda.findMeTauntSumBlood
import lin.warExt.rival.findRivalAtcSum
import lin.warExt.rival.rivalCanHurt
import lin.warExt.rival.rivalCardsByPlayArea

/**
 * todo 数量可以用权重解决
 */
abstract class ReleaseWar(var warCardGap: Int, var rivalAtc: Int, var lessBlood: Int) : AbsWeightCondition(),
    RoundLifecycle, UseBeforeStrategy {
    companion object {
        //无伤害视为全部清理
        const val ALL_CLEAN: Int = 0
    }
    override fun name(): String {
        return "解场用的之aoe"
    }
    val cache = mutableMapOf<String, Int>()
    private var clean = true
    private var first = true
    private val useBeforeStrategy: MutableList<UseBeforeStrategy> by lazy { mutableListOf(this) }

    //todo 实现每个回合执行一次
    override fun start(warInfo: WarInfo) {
        clean = true
        first = true
    }
    override fun calculateWeight(callCard: ComboCard, warInfo: WarInfo): Double {

        val weight = calWeight(callCard.cardId(), warInfo)
        if (weight + callCard.powerWeight > NotWeight) {
            callCard.useBeforeStrategy?.add(this) ?: run { callCard.useBeforeStrategy = useBeforeStrategy }
            callCard.useGroupId = CleanWarId
            return weight
        }
        //todo 存在会打出情况
        return weight
    }

    override fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        warInfo.cleanPlayByRoundOnce()
        if (calWeight(comboCard.cardId(), warInfo) == UnUseWeight) {
            comboCard.unUse()
        }

    }

    private fun calWeight(id: String, warInfo: WarInfo): Double {
        val damage = cache.getOrPut(id) {
            calDamage(id)
        }

        val rivalCards = warInfo.rivalCardsByPlayArea().filter { it.canHurt() }
        val extRivalNum = calExtRivalNum(rivalCards, warInfo)

        val meCards = warInfo.rivalCanHurt()
        var meCardsNun = meCards.size
        if (damage != ALL_CLEAN) {
            meCardsNun = meCards.filter { it.blood() <= damage }.size
        }
        var warCardGap = extRivalNum - meCardsNun

        if (extRivalNum >= this.warCardGap && warInfo.cleanPlayByRoundOnce()) {
            warCardGap = extRivalNum - meCardsNun
        }

        if (warCardGap >= this.warCardGap) {
            var meetNum = rivalCards.size
            if (damage != ALL_CLEAN) {
                meetNum = rivalCards.filter { it.blood() <= damage }.size
            }
            var extWeight = 0.0
            if (meetNum != 0) {
                extWeight = meetNum.toDouble() / rivalCards.size
            }

            return groupWeight + extWeight
        }


        //场攻大于12
        if (damage == ALL_CLEAN) { //无视条件
            val rivalAtc = warInfo.findRivalAtcSum() - warInfo.findAtcSum()
            if (rivalAtc > this.rivalAtc) return groupWeight
        }


        return UnUseWeight
    }

    /**
     * 获取额外数量,类似数量加权
     */
    private fun calExtRivalNum(rivalCards: List<Card>, warInfo: WarInfo): Int {
        var extRivalNum = rivalCards.size
        if (extRivalNum < warCardGap) {
            val meBlood = warInfo.meBlood() + warInfo.findMeTauntSumBlood()
            if (meBlood - rivalAtc < lessBlood) extRivalNum++ //血量少判断数量加一
            else {
                //含有特殊随从判断加一
                if (rivalCards.isExtWeight()) extRivalNum++
            }
        }
        return extRivalNum

    }
}

class DepNumRelWar() : ReleaseWar(0, 0, 0) {

    val atcFactory = 3
    val bloodFactory = 2
    override fun setNum(num: Int) {
        this.warCardGap = num
        this.rivalAtc = num * atcFactory
        this.lessBlood = num * bloodFactory

    }
}

//todo-future 未整理
fun calDamage(id: String): Int {
    val cardText = getText(id)
    val damage = extractNumber(cardText)
    if (damage == ALL_CLEAN) return ALL_CLEAN
    val count = extractCount(cardText)
    return damage * count
}

fun extractNumber(text: String): Int {
    // 使用正则表达式匹配整数（包括负数）
    val regex = """[$]?(\d+)点伤害""".toRegex()
    val match = regex.find(text)
    return match?.value?.toIntOrNull() ?: 0
}

fun getText(id: String): String {
    return CardUtil.getCardText(id) ?: ""
}

// 中文数字映射表（支持 0~10，可根据需要扩展）
val chineseToNumber = mapOf(
    "零" to 0, "一" to 1, "二" to 2, "两" to 2, "三" to 3, "四" to 4,
    "五" to 5, "六" to 6, "七" to 7, "八" to 8, "九" to 9, "十" to 10
)

// 提取“次”前面的中文数字（如“三次”），默认为1
fun extractCount(text: String): Int {
    // 匹配中文数字 + “次”
    val countRegex = """([一二三四五六七八九十两零]+)次""".toRegex()
    val match = countRegex.find(text)
    val chineseNum = match?.groupValues?.get(1)

    return if (chineseNum != null) {
        // 简单处理：只支持单个中文数字（如“三”、“十”），不处理“二十三”等复合
        chineseToNumber[chineseNum] ?: 1
    } else {
        1 // 默认1次
    }
}

