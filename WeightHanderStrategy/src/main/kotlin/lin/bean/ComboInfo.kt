package lin.bean

import lin.weightHandler.condition.context.NotWeight
import lin.weightHandler.condition.context.OrderWeight

/**
 * @param depIds 数据库用String,用","分割
 */
class ComboInfo(val infoId:Int, val bindId: Double, val isBefore: Boolean, val depIds: Array<Double>, val comboWeight: Double)

/**
 *@param comboId todo-future  comboId 不知道有没有用了
 * @param priority todo-future 感觉可以删除了
 */
open class Combo(val comboId: Int, val comboRule: ComboRule, val priority: Boolean) {
    open fun comboProcess(callComboCard: ComboCard, comboCard: ComboCard): Double {
        return comboRule.let {
            val weight = it(comboCard)
            //优先级处理
            if (weight != NotWeight) {//表示是同一组
                val powerWeight = callComboCard.powerWeight
                //之前策略
                if (priority && powerWeight <= comboCard.powerWeight) {//增加权重
                    val addWeight = comboCard.powerWeight - powerWeight + OrderWeight //保证同组优先级最高
                    callComboCard.addWeight(addWeight)
                }

                //之后策略
                if (!priority && powerWeight >= comboCard.powerWeight) {//之后 如果不是最小,修正权重为最小
                    val addWeight = powerWeight - comboCard.powerWeight + OrderWeight //保证同组优先级最高
                    comboCard.addWeight(addWeight)
                }
            }
            weight
        }

    }
}

object DefCombo : Combo(0, { _ -> NotWeight }, false) {
    override fun comboProcess(callComboCard: ComboCard, comboCard: ComboCard): Double {
        return NotWeight
    }
}