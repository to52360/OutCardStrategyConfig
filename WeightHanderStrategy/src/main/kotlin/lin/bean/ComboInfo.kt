package lin.bean

import lin.myLog
import lin.weightHandler.condition.context.NotWeight
import lin.weightHandler.condition.context.OrderWeight

/**
 * @param depIds 数据库用String,用","分割
 */
class ComboInfo(
    val infoId: Int,
    val bindId: Double,
    val comboType: ComboType,
    val depIds: Array<Double>,
    val comboWeight: Double
)

/**
 *@param comboId todo-future  comboId 不知道有没有用了
 * @param comboType todo-future 感觉可以删除了
 */
open class Combo(val comboId: Int, val comboRule: ComboRule, val comboType: ComboType) {
    open fun comboProcess(callComboCard: ComboCard, comboCard: ComboCard): Double {
        return comboRule.let {
            val weight = it(comboCard)
            //优先级处理
            if (weight != NotWeight) {//表示是同一组
                val beforeWeight = callComboCard.powerWeight
                if (ComboType.BEFORE == comboType) {
                    //之前策略
                    if (beforeWeight <= comboCard.powerWeight) {//增加权重
                        val addWeight = comboCard.powerWeight - beforeWeight + OrderWeight //保证同组优先级最高
                        callComboCard.addWeight(addWeight)
                    }
                    myLog.info { "核心权重${callComboCard.powerWeight},成员权重${comboCard.powerWeight}" }
                }
            }
            weight

        }

    }
}

enum class ComboType {
    BEFORE,
    DEF,
    AFTER,
    ;

    companion object {
        fun fromString(str: String?): ComboType {
            if (str.isNullOrBlank()) return DEF
            return try {
                ComboType.valueOf(str.uppercase())
            } catch (_: Exception) {
                DEF
            }
        }
    }

}

object DefCombo : Combo(0, { _ -> NotWeight }, ComboType.DEF) {
    override fun comboProcess(callComboCard: ComboCard, comboCard: ComboCard): Double {
        return NotWeight
    }
}

val DefCombos = listOf<Combo>(DefCombo)