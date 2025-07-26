package lin.serviceLoader.cardRule

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.domain.MyWarInfo

//减轻 判断的类型处理都在这里

/**
 * 手牌区域为条件
 *
 */
interface HandArea : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo)=
        onWarInfoProcessWeight(callCard,myWarInfo.getHandComboCards())
     fun onWarInfoProcessWeight(callCard: ComboCard, handCards: List<ComboCard>)
}

/**
 *墓场
 */
interface GraveyardArea : WeightCondition {
    override fun calculateSetWeight(callCard: ComboCard, myWarInfo: MyWarInfo)= onWarInfoProcessWeight(myWarInfo.getGraveyardCards())
    fun onWarInfoProcessWeight(graveyardCards: List<Card>)
}




//可以一起打出
interface ComboCondition{

    //收益组默认之后打出
     // todo-future 之前也没有实现
    fun after() = true

}



