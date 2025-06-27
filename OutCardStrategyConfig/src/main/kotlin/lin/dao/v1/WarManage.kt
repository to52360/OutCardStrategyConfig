package lin.dao.v1

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import lin.bean.v2.ComboWeightInfo

class WarManage(private val war:War,private val infoMap : Map<String, ComboWeightInfo> ) {
    private var handCards = emptyList<Card>()
    private var comboCards  = emptyList<ComboCard>()

    init {
        handCards = war.me.handArea.cards.toList()
    }




    //转化
    private  fun parseComboCard():List<ComboCard>{
        //todo 等待给orderComBoCard赋值
        handCards
        infoMap

        TODO("")
    }
    //重新加载战场信息 以后再考虑刷新方式性能问题
    fun reloadWarInfo(){
        handCards = war.me.handArea.cards.toList() //这里是复制

    }

    fun getHandComboCards()=handCards
    //重新加载
    fun refresh(){
        //这里只有转化没有排序
        comboCards = parseComboCard()
    }
    private fun getCanUseCards()=comboCards.filter {
            comBoCard ->  comBoCard.card.cost<= getNowCost()
    }
    //执行条件计算权重,排除负数权重,排序,过滤出合法的卡牌
    fun runConditionAndOrderWeight():List<ComboCard>{
        parseComboCard()
        getCanUseCards()
        TODO("执行条件")
    }

    //todo 不一定能使用出去  打不出去尝试指向关联组
    fun useCard(comBoCard: ComboCard):Boolean{
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
     fun getNowCost()= war.me.usableResource
    //有费用
    fun hasCost()=getNowCost()>0


}