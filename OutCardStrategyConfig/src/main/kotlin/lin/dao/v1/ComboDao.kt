package lin.dao.v1

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log

import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import lin.bean.v2.ComboWeightGroup
import lin.strategy.ComboStrategy

import lin.bean.v2.ComboWeightInfo
import lin.dao.v1.bean.ComBoCard


/**
 * todo 名字还没有想好
 * 参考数据
 * 执行攻击动作
 * [club.xiaojiawei.util.DeckStrategyUtil.Result.execAction]
 * mapstruct DaoDao复制 Mapper
 *
 * []
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */
 class ComboDao(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>) {
    //存储转化权重信息
    private val infoMap : Map<String, ComboWeightInfo> = emptyMap()
    //存储策略分组
    private val strategyMap : Map<String, ComboStrategy> = emptyMap()

    private val groupMap : Map<String,ComboWeightGroup> = emptyMap()


    /**
     * 没有权重信息或者没有匹配对应策略
     */
    private fun hasValidStrategy() : Boolean{
        return infoMap.isNotEmpty() && strategyMap.isNotEmpty()
    }

    /**
     * 返回出牌策略,给策略类
     */
    fun getOutCardLambda() : (War)-> Unit{

        TODO()
    }

    private fun executeOutCardStrategy(war :War) {

         val warManage = WarManage(war)//这里依赖局部变量,还是全局war属性呢?
          warManage.getCanUseCards()


    }





}

private val  defaultStrategy:HsRadicalDeckStrategy by lazy {
    HsRadicalDeckStrategy()
}
//默认策略
val defaultOutCardLambda : (War)-> Unit={
    defaultStrategy.executeOutCard()
}

