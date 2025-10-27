package drop.parse

import lin.bean.CardWeightInfo
import lin.domain.use.DiscoverUseStrategy
import lin.domain.use.UseAfterLClick
import lin.serviceLoader.parse.ParseCardWeightInfo

class DropParse : ParseCardWeightInfo {
    override fun parse(infoMap: Map<String, CardWeightInfo>) {
        //发现法术
        infoMap["TLC_451"]?.addUseStrategy(DiscoverUseStrategy)
        //发现地标
        infoMap["WON_103"]?.run {
            addUseStrategy(UseAfterLClick)
            //地标不触发发现,取消该逻辑
            //addUseStrategy(DiscoverUseStrategy)
        }
        //过期期货
        infoMap["ULD_163"]?.toDie = true
    }
}