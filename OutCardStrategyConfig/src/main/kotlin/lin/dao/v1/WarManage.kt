package lin.dao.v1

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import lin.dao.v1.bean.ComBoCard

class WarManage(private val war:War) {
    private var handCards = emptyList<Card>()
    private var orderComBoCard  = emptyList<ComBoCard>()
    init {
        handCards = war.me.handArea.cards.toList()
        orderComBoCard = parseComboCardAndOrderByWeight()
    }

    fun comBoGroup():ComboGroup{
        val comboGroup = parseComboCardAndOrderByWeight()

        TODO()
    }


    //转化并排序
    private  fun parseComboCardAndOrderByWeight():List<ComBoCard>{
        handCards
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
        orderComBoCard = parseComboCardAndOrderByWeight()
    }
    fun getCanUseCards()=orderComBoCard.filter {
            comBoCard ->  comBoCard.card.cost<= war.me.usableResource
    }


}