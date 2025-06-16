package lin.strategy.condition

import lin.bean.v2.ComboWeightInfo

/**
 *
 */
class PlayArea(private val cards :  Map<String, ComboWeightInfo>) : OutCardCondition() {
        override fun id() = 32.0
        fun canUse():Boolean{

            TODO()
        }


}