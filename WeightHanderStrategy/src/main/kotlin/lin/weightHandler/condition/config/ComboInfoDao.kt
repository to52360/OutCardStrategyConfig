package lin.weightHandler.condition.config

import lin.serviceLoader.combo.ComboInfo

class ComboInfoDao {
    fun getAllCombos(): List<ComboInfo> {
       return listOf(ComboInfo(1,1.4,true,arrayOf(1.3,1.1), 10.0))
    }
}