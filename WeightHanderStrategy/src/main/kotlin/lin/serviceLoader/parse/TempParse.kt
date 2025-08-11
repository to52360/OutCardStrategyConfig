package lin.serviceLoader.parse

import lin.bean.CardWeightInfo
import lin.bean.DiscoverUseStrategy
import lin.bean.UseAfterLClick

class TempParse : ParseCardWeightInfo {
    override fun parse(infoMap: Map<String, CardWeightInfo>) {
        infoMap["TLC_451"]?.addUseStrategy(DiscoverUseStrategy)
        infoMap["WON_103"]?.run {
            addUseStrategy(UseAfterLClick)
            addUseStrategy(DiscoverUseStrategy)

        }
    }
}