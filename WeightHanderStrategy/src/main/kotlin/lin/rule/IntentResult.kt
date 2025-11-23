package lin.rule

import lin.domain.context.NotWeight


/**
 * 可有意图的结果
 */
sealed class IntentResult(var weight: Double = NotWeight)
class EnableResult(
    weight: Double, val modifyCard: ComboCardAction? = null
) : IntentResult(weight)


//跳过
object SkipResult : IntentResult()

//强制停止
object StopResult : IntentResult()












