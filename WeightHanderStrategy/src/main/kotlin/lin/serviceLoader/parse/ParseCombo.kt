package lin.serviceLoader.parse

import lin.bean.*
import lin.domain.context.NotWeight
import lin.myLog
import lin.weightHandler.condition.config.ComboInfoDao
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
                    //最后打出
                    ComboType.AFTER -> {
                        bindCardGroup.forEach {
                            it.lastUse = LastUse(comboInfo.comboWeight)
                        }
                    }

                    //起始换牌
                    ComboType.CHANGE -> {
                        val comboRule: ComboRule = getRule()
                        bindCardGroup.forEach { it.addChangeComboRule(comboRule) }
                    }

                    // 默认情况
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