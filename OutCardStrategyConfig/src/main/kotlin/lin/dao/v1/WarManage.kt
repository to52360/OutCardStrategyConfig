package lin.dao.v1

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import lin.bean.v2.ComboWeightInfo
import lin.dao.v1.bean.ComBoCard

class WarManage(private val war:War,private val infoMap : Map<String, ComboWeightInfo> ) {
    private var handCards = emptyList<Card>()
    private var orderComBoCard  = emptyList<ComBoCard>()
    init {
        handCards = war.me.handArea.cards.toList()
        orderComBoCard = parseComboCard()
    }






    //转化并排序
    private  fun parseComboCard():List<ComBoCard>{
        handCards
        infoMap
        //对手
        //战场都要复制comboGroup
        TODO()
    }
    //重新加载战场信息 以后再考虑刷新方式性能问题
    fun reloadWarInfo(){
        handCards = war.me.handArea.cards.toList()

    }
    //重新再排序
    fun refreshComboOrder(){
        orderComBoCard = parseComboCard()
    }
    fun getCanUseCards()=orderComBoCard.filter {
            comBoCard ->  comBoCard.card.cost<= war.me.usableResource
    }


}