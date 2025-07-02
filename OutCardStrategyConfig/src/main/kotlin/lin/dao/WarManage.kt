package lin.dao

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import lin.bean.ComboWeightInfo
//可见性 控制
interface WarInfo{
   fun getCanUseCards():List<ComboCard>
   fun getHandComboCards():List<ComboCard>
   fun getGraveyardCards():List<Card>
   fun getNowCost():Int
   fun costNotFillAllCard():Boolean
}
interface WarCtrl {
    fun refresh()
    fun useCard(comBoCard: ComboCard):Boolean
    fun parseComboCard():List<ComboCard>
}
interface WarPro: WarInfo, WarCtrl {

}

//todo 东西太多,功能也太多了
class WarManage(private val war:War,private val infoMap : Map<String, ComboWeightInfo> ) : WarPro {
    private var handCards = emptyList<Card>()
    private var comboCards  = emptyList<ComboCard>()
    private var canUseCards = emptyList<ComboCard>()





    //转化
    override fun parseComboCard():List<ComboCard>{
        //todo 等待给orderComBoCard赋值
        handCards
        infoMap

        TODO("")
    }
    //重新加载战场信息 以后再考虑刷新方式性能问题
    fun reloadWarInfo(){
        handCards = war.me.handArea.cards.toList() //这里是复制

    }

    override fun getHandComboCards()=comboCards
    override fun getGraveyardCards()=war.me.graveyardArea.cards

    //重新加载
    override fun refresh(){
        //这里只有转化没有排序
        handCards = war.me.handArea.cards.toList()
        comboCards = parseComboCard()
        canUseCards = canUseCards()
    }
    override fun getCanUseCards()= canUseCards
    private fun canUseCards()=comboCards.filter {
            comBoCard ->  comBoCard.card.cost<= getNowCost()
    }

    //todo 不一定能使用出去  打不出去尝试指向关联组 ,该方法好像也不符合战场范畴
    override fun useCard(comBoCard: ComboCard):Boolean{
        //要更改autoPower逻辑
        comBoCard.card.action.autoPower()
        TODO()
    }
    //足够费用直接使用
    fun tryUseCard(comBoCard: ComboCard):Boolean{
      return  if(comBoCard.card.cost==getNowCost()){
            TODO("")
        }else false
    }
    //todo 不知道并发安全不,执行出牌策略和更新war是不是同一个线程
    override fun getNowCost()= war.me.usableResource
    override fun costNotFillAllCard(): Boolean {
        TODO("Not yet implemented")
    }


    //有费用
    fun hasCost()=getNowCost()>0


}