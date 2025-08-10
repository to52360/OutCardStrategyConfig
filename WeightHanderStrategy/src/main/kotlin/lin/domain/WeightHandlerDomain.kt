package lin.domain

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.bean.FindStage
import lin.myLog

import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.base.getNowCost
import lin.weightHandler.CardWeightHandler
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import kotlin.math.absoluteValue


class WeightHandlerDomain(private val warManage: MyWarManage) {
    private val weightHandlers: List<WeightHandler>
    private val cardWeightHandlers: List<CardWeightHandler>

    init {
        try {
            val infos = warManage.infoMap
            val cardWeightInfos =  infos.values.toList()
            val services =ServiceLoaderUtils.loadServices(WeightHandler::class.java)
            val cardWeightHandler = mutableListOf<CardWeightHandler>()
            val weightHandler = services.sortedBy {
                //按ai的说法会语义多重,实践看看有什么后果
                if(it is InitHandler) {
                    it.init(cardWeightInfos)
                }
                //todo 存在一个问题没法单独扩展
                if(it is CardWeightHandler){
                    cardWeightHandler.add(it)
                }
                it.priority()
            }
            this.weightHandlers = weightHandler
            this.cardWeightHandlers = cardWeightHandler.toList()

        } catch (e: Exception) {
            e.printStackTrace()
            myLog.error(e) { "测试化失败" }
            throw e
        }


    }

    fun processWeight(weightResult: EndWeightResult): EndWeightResult {
        weightResult.canUseCards.forEach { comboCard ->
            weightHandlers.forEach { it.cardWeightProcess(comboCard, warManage) }
            weightResult.processWeightAfterAdd(comboCard)
        }
        return weightResult
    }


    fun findCombination(
        cost: Int = warManage.getNowCost(),
        canUseCardsByCost: List<ComboCard> = warManage.canUseCards
    ): WeightResult {
        if (canUseCardsByCost.isEmpty()) return EmptyWeightResult
        val canUseCardByOrder = canUseCardsByCost.sortedByDescending { it.powerWeight }
        val weightResultByStrategy = processStrategy(canUseCardByOrder)
        if (weightResultByStrategy != EmptyWeightResult) return weightResultByStrategy


        //todo-fut
        val weightResult = EndWeightResult(canUseCardByOrder, cost)
        if (weightResult.unAbleUseCards()) return EmptyWeightResult
        val weightResultByProcess = processStrategy(weightResult.canUseCardsByHandler.toList())
        if (weightResultByProcess != EmptyWeightResult) return weightResultByProcess
        if (weightResult.isLessCost()) return weightResult
        weightResult.findBestCombination()
        return weightResult
    }

    private fun processStrategy(canUseCardByOrder: List<ComboCard>): WeightResult {
        val useStrategy = canUseCardByOrder.first().useStrategy
        if (useStrategy is FindStage) {
            return useStrategy.find(warManage, TODO())
        }
        return EmptyWeightResult
    }











    /**
     * 存在性能问题,暂时这样了
     */
    fun List<ComboCard>.copy(skipComboCard: ComboCard): List<ComboCard> {
        val comboCards = mutableListOf<ComboCard>()
        forEach {
            if (skipComboCard != it.card) {//重写的equals,不知道==起效不
                val comboCard = warManage.parseComboCard(it.card)
                comboCards.add(comboCard)
            }

        }
        return comboCards
    }
    /**
     * 发现策略
     */
    fun executeDiscoverChooseCard(vararg cards: Card): Int{
        var maxIndex = 0
        var maxWeight = 0.0
        for(i in cards.indices){
            val comboCard = warManage.parseComboCard(cards[i])
            cardWeightHandlers.forEach {
                it.cardWeight(comboCard)
            }
            comboCard.basePowerWeight
            val extWeight = comboCard.powerWeight + pointToDouble(comboCard.basePowerWeight)
            if (extWeight > maxWeight) {
                myLog.info { "暂时最大值:id:${comboCard.card.cardId},名字:${comboCard.card.entityName}的发现权重:$extWeight" }
                maxWeight = comboCard.powerWeight
                maxIndex = i
            }
        }

        return maxIndex

    }

    fun pointToDouble(number: Double): Double {
        val absNum = number.absoluteValue
        // 1. 获取小数部分
        val decimalPart = absNum - absNum.toInt() // 结果: 0.14159
        if (decimalPart == 0.0) return decimalPart
        // 2. 将小数部分乘以 10^n（n 是你想要保留的小数位数），然后转换为 Int
        // 例如，保留 5 位小数
        val scaleFactor = 100 // 10^5
        val result = decimalPart * scaleFactor
        return result


    }



}