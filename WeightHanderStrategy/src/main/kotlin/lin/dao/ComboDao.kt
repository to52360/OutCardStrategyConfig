package lin.dao


import club.xiaojiawei.bean.War


import lin.bean.ComboCard
import lin.myLog

import lin.weightHandler.condition.bean.AddCost
import lin.weightHandler.condition.bean.CardType.*
import lin.weightHandler.condition.context.ConditionException


/**
 * todo-future 名字还没有想好
 * 参考数据
 * 执行攻击动作
 * [club.xiaojiawei.util.DeckStrategyUtil.Result.execAction]
 * mapstruct DaoDao复制 Mapper
 *
 * []
 * 出牌条件 先打出组里 16.1   .1策略先打出条件为16.0卡
 */

class ComboDao( war: War) {

    //SPI没法抛异常把把val 改为 lateinit var
    //存储转化权重信息
    private lateinit var warManage: MyWarManage
    private lateinit var weightHandlerDao:WeightHandlerDao
    var initResult = true

    //存储策略分组
    init {
        myLog.info{
            "ComboDao初始化"
        }
        val  classLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = this::class.java.classLoader
            warManage = MyWarManage(war)
            weightHandlerDao = WeightHandlerDao(warManage = warManage)
        } catch (e: Exception) {
            e.printStackTrace()
            myLog.error(e) { "初始化失败" }
            initResult = false
        }finally {
            Thread.currentThread().contextClassLoader = classLoader
        }


    }


    /**
     * 出牌策略
     */
     fun outCardStrategy() {
        warManage.executeEnvironment {
            //获取能够打出的卡牌
            val canUseCardsByCost = warManage.getCanUseCardsByCost()
            //todo 看一下isChange能不能放入weightHandlerDao
            val bestCombination = findBestCombination(canUseCardsByCost,false)

            val canUseCardsByHandler = weightHandlerDao.canUseCardsByHandler
            myLog.info { "找到需要使用的卡牌:$bestCombination" }
            executeUseCard(canUseCardsByHandler,bestCombination)

        }
    }

    private fun findBestCombination(cards:List<ComboCard>, isChange:Boolean):List<ComboCard>{
        if(isChange){//todo 这方案不太靠谱 重新计算权重并使用
            weightHandlerDao.executeHandChaWeightProcess(cards)
        }else{
            weightHandlerDao.executeWeightProcess(cards)
        }

        val canUseCardsByHandler = weightHandlerDao.findBeforeOrBestCombination()

        when(canUseCardsByHandler.size){
            0-> return emptyList()
            1->{
                val comboCard = canUseCardsByHandler.first()
                when(comboCard.cardType){
                    DEFAULT ->  return canUseCardsByHandler
                    CHANGE ->{
                        warManage.useCardAndRemove(canUseCardsByHandler.first())
                        warManage.refreshComboCards()
                        //todo 这方案不太靠谱 重新计算权重并使用
                        return findBestCombination(warManage.getCanUseCardsByCost(),true)
                    }
                    ADD_COST -> {
                        val expectCost =   comboCard.getMetadata(AddCost)
                        if(expectCost==null) throw ConditionException("没有费用相关信息")
                        else{
                            return  weightHandlerDao.findBestCombination(comboCard,expectCost)
                        }

                    }

                }
            }
            else ->{
                return canUseCardsByHandler
            }

        }

    }

    /**
     * @param canUseCardsByHandler 全部能打的卡牌
     * @param bestCombination 回溯算法获取组合
     */
    private fun executeUseCard(canUseCardsByHandler: List<ComboCard>, bestCombination: List<ComboCard>) {
        // 5. 执行找到的最佳出牌组合
        if (bestCombination.isNotEmpty()) {
            if(bestCombination.size==1){
                warManage.useCard(bestCombination.first())
                return
            }
            val finalCost = bestCombination.sumOf { it.getCost() }

            myLog.info {
                val finalWeight = bestCombination.sumOf { it.varPowerWeight }
                val msg =
                    "找到最优出牌组合 (总费用: $finalCost, 总权重: $finalWeight): ${bestCombination.map { it.card.cardId }}"
                 msg
            }


            val expectCost = warManage.getNowCost() - finalCost
            bestCombination.forEach { warManage.useCard(it) }
            //todo-future 直接遍历使用
            if (warManage.getNowCost() > expectCost) {//说明有些牌没打出去,通过补偿
                val moreTryCard = canUseCardsByHandler - bestCombination.toSet()
                if (moreTryCard.isNotEmpty()) {
                    for (card in moreTryCard) {
                        if (warManage.getNowCost() >= card.getCost()) {
                            warManage.useCard(card)
                            if (!warManage.hasCost()) break
                        }
                    }
                }


            }
        }
    }


}




