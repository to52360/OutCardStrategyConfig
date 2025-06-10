package lin.d

import club.xiaojiawei.bean.War
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.status.WAR

class CustomiseD(private val war : War) {
    init {
        val me = war.me
        val rival = war.rival
        var plays = me.playArea.cards.toList()
    }

    fun isValid(): Boolean {
       return war.isValid();

    }

}