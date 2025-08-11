package lin.serviceLoader.parse

import lin.bean.CardWeightInfo
import lin.bean.Combo
import lin.bean.ComboRule
import lin.bean.ComboType
import lin.myLog
import lin.weightHandler.condition.config.ComboInfoDao
import lin.weightHandler.condition.context.NotWeight
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ParseCombo : ParseCardWeightInfo, KoinComponent {
    override fun parse(infoMap: Map<String, CardWeightInfo>) {
        val cardGroupInfos = infoMap.values.groupBy { it.groupId }
        val comboInfoDao = get<ComboInfoDao>()
        val comboInfos = comboInfoDao.findAll()
        comboInfos.forEach { comboInfo ->
            val bindId = comboInfo.bindId
            val bindCardGroup = cardGroupInfos[bindId]
            bindCardGroup?.let { comboGroup ->
                fun getRule(): ComboRule {
                    val comboRule: ComboRule = { comboCards ->
                        if (comboInfo.depIds.any {
                                it == comboCards.groupId()
                            })
                            comboInfo.comboWeight
                        else
                            NotWeight
                    }
                    return comboRule
                }
                when (comboInfo.comboType) {
                    ComboType.AFTER -> {
                        bindCardGroup.forEach {
                            it.lastUse = true
                        }
                    }

                    ComboType.CHANGE -> {
                        val comboRule: ComboRule = getRule()
                        bindCardGroup.forEach { it.addChangeComboRule(comboRule) }
                    }

                    else -> {
                        val comboRule: ComboRule = getRule()
                        val combo = Combo(comboInfo.infoId, comboRule, comboInfo.comboType)
                        //赋值
                        bindCardGroup.forEach {
                            it.addCombo(combo)
                        }
                    }
                }


            } ?: run {
                myLog.warn { "绑定在权重表没有找到对应信息,id为${bindId}" }
            }


        }
    }
}