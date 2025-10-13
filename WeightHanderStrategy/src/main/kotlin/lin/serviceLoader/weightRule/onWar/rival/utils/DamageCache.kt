package lin.serviceLoader.weightRule.onWar.rival.utils

import club.xiaojiawei.hsscriptcardsdk.util.CardUtil
import lin.bean.ComboCard
import lin.bean.comboCardUtils.base.atc
import lin.bean.comboCardUtils.base.isMinion
import lin.myLog
import lin.serviceLoader.weightRule.onWar.rival.CleanWar.Companion.ALL_CLEAN


/**
 * 伤害的处理
 */
class DamageCache {
    val cache = mutableMapOf<String, Int>()
    fun getDamageById(callCard: ComboCard): Int {
        val damage = cache.getOrPut(callCard.cardId()) {
            calDamage(callCard)
        }
        if (damage == ALL_CLEAN && callCard.isMinion()) {
            return callCard.atc()
        }
        myLog.info { "解析到伤害值为:${damage}" }
        return damage
    }

}

//todo-future 未整理
fun calDamage(card: ComboCard): Int {
    val cardText = getText(card.cardId())
    val damage = extractNumber(cardText)
    val count = extractCount(cardText)
    return damage * count
}

fun extractNumber(text: String): Int {
    // 使用正则表达式匹配整数（包括负数）
    val regex = """\$?(\d+)点伤害""".toRegex()
    val match = regex.find(text)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
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