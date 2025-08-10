package lin.bean

import lin.domain.MyWarManage
import lin.domain.WeightHandlerDomain
import lin.domain.WeightResult

interface FindStage {
    fun find(warManage: MyWarManage, weightHandlerDomain: WeightHandlerDomain): WeightResult
}

sealed class UseStrategy(val useType: UseType)
data object DefUseStrategy : UseStrategy(UseType.DEF)

//
data object ChangeStrategy : UseStrategy(UseType.BEFORE)
class AddCostStrategy(val cost: Int) : UseStrategy(UseType.BEFORE)

class ExtCostStrategy(val cost: Int) : UseStrategy(UseType.BEFORE), FindStage {
    override fun find(
        warManage: MyWarManage,
        weightHandlerDomain: WeightHandlerDomain
    ): WeightResult {
        TODO("Not yet implemented")
    }

}

object AfterStrategy : UseStrategy(UseType.AFTER)


enum class UseType {
    BEFORE,
    DEF,
    AFTER
}