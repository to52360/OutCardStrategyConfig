package lin.weightHandler.condition.define

import club.xiaojiawei.bean.Card
import lin.dao.ComboCard
import lin.dao.WarInfo

//减轻 判断的类型处理都在这里

/**
 * 手牌区域为条件
 *
 */
interface HandArea : DefaultOutCardCondition {
    override fun onWarInfoProcessWeight(callCard: ComboCard, warInfo: WarInfo)=
        onWarInfoProcessWeight(callCard,warInfo.getHandComboCards())
     fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>)
}

/**
 *墓场
 */
interface GraveyardArea : DefaultOutCardCondition {
    override fun onWarInfoProcessWeight(callCard: ComboCard, warInfo: WarInfo)= onWarInfoProcessWeight(warInfo.getGraveyardCards())
    fun onWarInfoProcessWeight(graveyardCards: List<Card>)
}

//默认
interface DefaultOutCardCondition: OutCardCondition, DepByWeightInfo

//可以一起打出
interface ComboCondition{

    //收益组默认之后打出
     // todo-future 之前也没有实现
    fun after() = true

}



