package lin.domain.combo

import lin.bean.CardWeightInfo
import lin.bean.ComboInfo
import lin.myLog

const val LAST = "last"
const val BEFORE = "before"
const val DEF = "def"
const val CHANGE = "change"


interface ComboParse {
    fun id(): String = this::class.java.simpleName
    fun parse(cardGroupInfos: Map<Double, List<CardWeightInfo>>, comboInfo: ComboInfo)
}

interface ValidBindComboParse : ComboParse, ValidExistWeightInfo {
    override fun parse(cardGroupInfos: Map<Double, List<CardWeightInfo>>, comboInfo: ComboInfo) {
        val validResult = valid(cardGroupInfos, comboInfo.bindId)
        if (validResult.isNotEmpty()) {
            if (extProcess(cardGroupInfos, comboInfo))
                processBindCombo(validResult, comboInfo)
        }
    }

    fun extProcess(cardGroupInfos: Map<Double, List<CardWeightInfo>>, comboInfo: ComboInfo): Boolean = true
    fun processBindCombo(cardWeightInfos: List<CardWeightInfo>, comboInfo: ComboInfo)

}

/**
 * 验证权重表是否存在数据
 */
interface ValidExistWeightInfo {
    fun valid(cardWeightInfos: Map<Double, List<CardWeightInfo>>, bindId: Double): List<CardWeightInfo> {
        val bindCardGroup = cardWeightInfos[bindId]
        if (bindCardGroup == null) {
            myLog.warn { "绑定在权重表没有找到对应信息,id为${bindId}" }
            return emptyList()
        }
        return bindCardGroup
    }
}

/**
 * 验证依赖是否存在
 */
interface ValidDepComboParse : ValidBindComboParse {
    override fun extProcess(cardGroupInfos: Map<Double, List<CardWeightInfo>>, comboInfo: ComboInfo): Boolean {
        if (comboInfo.depIds.isEmpty()) {
            myLog.warn { "需要的依赖不存在" }
            return false
        }
        for (depId in comboInfo.depIds) {
            val validResult = valid(cardGroupInfos, depId)
            if (validResult.isEmpty()) return false
        }
        return true
    }
}
