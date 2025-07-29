package lin.domain


import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import lin.bean.*


import lin.myLog
import lin.utils.JarClassLoader
import java.util.ServiceConfigurationError


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

class ComboDomain(war: War) {

    //SPI没法抛异常把把val 改为 lateinit var
    //存储转化权重信息
    private lateinit var warManage: MyWarManage
    private lateinit var weightHandlerDomain:WeightHandlerDomain


    //存储策略分组
    init {
        myLog.info{
            "ComboDao初始化"
        }
        val  threadClassLoader = Thread.currentThread().contextClassLoader
        try {
            val classLoader = JarClassLoader(parent = javaClass.classLoader).classLoader()?:run {
                log.warn { "没有获取到类加载器" }
                javaClass.classLoader
            }
            Thread.currentThread().contextClassLoader = classLoader
            warManage = MyWarManage(war)
            weightHandlerDomain = WeightHandlerDomain(warManage = warManage)
        } catch (serviceError: ServiceConfigurationError){
            serviceError.printStackTrace()
            myLog.error(serviceError) { "serviceError初始化失败" }
        } finally {
            Thread.currentThread().contextClassLoader = threadClassLoader
        }


    }


    /**
     * 出牌策略
     */
     fun outCardStrategy() {
        myLog.info { "执行出牌策略" }


        warManage.executeEnvironment {
            //获取能够打出的卡牌
            val canUseCardsByCost = warManage.canUseCards
            //todo 看一下isChange能不能放入weightHandlerDao
            val bestCombination = findBestCombination(canUseCardsByCost,false)

            val canUseCardsByHandler = weightHandlerDomain.readCanUseCardsByHandler
            myLog.info { "找到需要使用的卡牌:$bestCombination" }
            executeUseCard(canUseCardsByHandler,bestCombination)

        }
    }

    /**
     *  处理使用策略,权重转发给权重处理器模型处理
     */
    private fun findBestCombination(cards:List<ComboCard>, isChange:Boolean):List<ComboCard>{
        if(isChange){//todo 这方案不太靠谱 重新计算权重并使用
            weightHandlerDomain.executeHandChaWeightProcess(cards)
        }else{
            weightHandlerDomain.executeWeightProcess(cards)
        }

        val canUseCardsByHandler = weightHandlerDomain.findBeforeOrBestCombination()

        when(canUseCardsByHandler.size){
            0-> return emptyList()
            1->{
                val comboCard = canUseCardsByHandler.first()
                when(val useStrategy = comboCard.useStrategy){
                    DefUseStrategy ->  return canUseCardsByHandler
                    ChangeStrategy ->{
                        warManage.useCardAndRemove(canUseCardsByHandler.first())
                        warManage.refreshComboCards()
                        //todo 这方案不太靠谱 重新计算权重并使用
                        return findBestCombination(warManage.canUseCards,true)
                    }
                    is AddCostStrategy -> {
                        val expectCost =   useStrategy.cost
                        return  weightHandlerDomain.findBestCombinationByExpectCost(comboCard,expectCost)
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

            //只有一个处理
            if(bestCombination.size==1){
                warManage.useCard(bestCombination.first())
                return
            }


            val needCost = bestCombination.sumOf { it.getCost() }
            myLog.info {
                val finalWeight = bestCombination.sumOf { it.powerWeight }
                val msg =
                    "找到最优出牌组合 (总费用: $needCost, 总权重: $finalWeight): ${bestCombination.map { it.card.cardId }}"
                 msg
            }

            //todo-future  打出优先级处理
/*            bestCombination.sortedBy {
                val combo =it.combo
                if(combo is ComboOrder){
                    combo.outCardPriority
                }else{
                    0
                }
            }*/


            val expectCost = warManage.getNowCost() - needCost
            bestCombination.forEach { warManage.useCard(it) }
            //todo-future 直接遍历使用
            //todo 还存在问题 ,万一新增卡牌
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

    fun executeDiscoverChooseCard(vararg cards: Card): Int{
        return weightHandlerDomain.executeDiscoverChooseCard(*cards)
    }

}




