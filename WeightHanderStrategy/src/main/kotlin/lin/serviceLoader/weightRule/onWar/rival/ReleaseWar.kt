package lin.serviceLoader.weightRule.onWar.rival

import club.xiaojiawei.hsscriptcardsdk.util.CardUtil
import lin.bean.CleanWarId
import lin.bean.ComboCard
import lin.domain.WarInfo
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.strategy.UseBeforeStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.lifecycle.RoundLifecycle
import lin.serviceLoader.weightRule.utils.abs.AbsWeightCondition
import lin.serviceLoader.weightRule.utils.war.CleanWarUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


/**
 * todo 数量可以用权重解决
 */
abstract class ReleaseWar(var warCardGap: Int, var rivalAtc: Int, var lessBlood: Int) : AbsWeightCondition(),
    RoundLifecycle, UseBeforeStrategy, KoinComponent {
    companion object {
        //无伤害视为全部清理
        const val ALL_CLEAN: Int = 0
    }
    override fun name(): String {
        return "解场用的之aoe"
    }
    val cleanWarUtils: CleanWarUtils = get<CleanWarUtils>()
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

        val weight = calWeight(callCard.cardId())
        if (weight + callCard.powerWeight > NotWeight) {
            callCard.useBeforeStrategy?.add(this) ?: run { callCard.useBeforeStrategy = useBeforeStrategy }
            callCard.useGroupId = CleanWarId
            return weight
        }
        //todo 存在会打出情况
        return weight
    }

    override fun extAction(comboCard: ComboCard, useStrategyUtils: UseStrategyUtils, warInfo: WarInfo) {
        cleanWarUtils.reload()
        if (calWeight(comboCard.cardId()) == UnUseWeight) {
            comboCard.unUse()
        }

    }

    private fun calWeight(id: String): Double {

        val damage = cache.getOrPut(id) {
            calDamage(id)
        }
        /*        if (damage == ALL_CLEAN) { //无视条件
                    if (cleanWarUtils.moreAtcThanGap(this.rivalAtc)) {
                        if(cleanWarUtils.cleanOnce(damage)&&cleanWarUtils.moreAtcThanGap(this.rivalAtc))
                            return groupWeight
                    }
                }*/
        if (cleanWarUtils.rivalNumLessGap(warCardGap)) {
            return UnUseWeight
        }
        cleanWarUtils.cleanOnce(damage)


        if (cleanWarUtils.lessGap(warCardGap, damage)) return UnUseWeight

        return groupWeight
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
    if (damage == ReleaseWar.Companion.ALL_CLEAN) return ReleaseWar.Companion.ALL_CLEAN
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

