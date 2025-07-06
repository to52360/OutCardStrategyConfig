package lin.dao.temp

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import club.xiaojiawei.bean.War


import lin.bean.v1.ComboWeightInfoV1


/**
 * todo 名字还没有想好
 * Entry(cardId: String,value: CardWeight)
 *
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */
class ComboWeightGroup(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>) {
    //存储转化权重信息
    private val infoMap : Map<String, ComboWeightInfoV1>

    //判断是否正确初始化,没有权重信息或者没有匹配对应策略情况
    private var useAble = true
    init{
        if(weightConfigs.isNotEmpty()) {
            infoMap = parseConfigInfo(weightConfigs)

        }else{
            infoMap = emptyMap()

            useAble = false
        }
    }

    /**
     * 没有权重信息或者没有匹配对应策略
     */
    private fun hasValidStrategy() : Boolean{
        return infoMap.isNotEmpty()
    }



    private fun executeOutCardStrategy(war :War) {

    }


    //把魔数转化为系统数据
    private fun parseConfigInfo(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>):Map<String, ComboWeightInfoV1> {
        return  weightConfigs.associateBy(
            keySelector = { it.key }
        ) { weightCard ->
            ComboWeightInfoV1(
                weightCard.value.weight.toInt(),
                DoubleSplitUtils.decimalInt(weightCard.value.weight),
                weightCard.value.powerWeight.toInt(),
                DoubleSplitUtils.decimalStr(weightCard.value.powerWeight)
            )
        }
    }

}




