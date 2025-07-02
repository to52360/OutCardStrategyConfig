package errorTest

import lin.strategy.ComboStrategy
import lin.strategy.def.CardInPlayOption
import kotlin.test.Test

//测试kotlin反射
class UnresolvedReference {
    @Test
    fun test1(){
        var comboStrategy: ComboStrategy = CardInPlayOption()
        println(comboStrategy)
    }
}