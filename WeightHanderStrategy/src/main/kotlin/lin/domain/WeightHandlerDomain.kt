package lin.domain

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.ComboCard
import lin.domain.combo.EmptyWeightResult
import lin.domain.combo.EndWeightResult
import lin.domain.combo.WeightResult
import lin.lifecycle.LifecycleRegister
import lin.myLog
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.base.getNowCost
import lin.weightHandler.DiscoverWeightHandler
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class WeightHandlerDomain(val warManage: MyWarManage) : KoinComponent {
    private val weightHandlers: List<WeightHandler>
    private val discoverWeightHandlers: List<DiscoverWeightHandler>

    init {
        try {
            val infos = warManage.infoMap
            val cardWeightInfos =  infos.values.toList()
            val services =ServiceLoaderUtils.loadServices(WeightHandler::class.java)
            val discoverWeightHandler = mutableListOf<DiscoverWeightHandler>()
            val lifecycle = get<LifecycleRegister>()
            val weightHandler = services.sortedBy {
                //按ai的说法会语义多重,实践看看有什么后果
                if(it is InitHandler) {
                    it.init(cardWeightInfos)
                }
                lifecycle.register(it)
                //todo 存在一个问题没法单独扩展发现策略
                if (it is DiscoverWeightHandler) {
                    discoverWeightHandler.add(it)
                }
                it.priority()
            }
            this.weightHandlers = weightHandler
            this.discoverWeightHandlers = discoverWeightHandler.toList()

        } catch (e: Exception) {
            e.printStackTrace()
            myLog.error(e) { "测试化失败" }
            throw e
        }


    }

    fun processWeight(weightResult: EndWeightResult) {
        weightResult.canUseCards.forEach { comboCard ->
            weightHandlers.forEach { it.cardWeightProcess(comboCard, warManage) }
            weightResult.processWeightAfterAdd(comboCard)
        }

    }


    fun findCombination(
        cost: Int = warManage.getNowCost(),
        canUseCardsByCost: List<ComboCard> = warManage.canUseCards
    ): WeightResult {
        val weightResult = EndWeightResult(canUseCardsByCost, cost)
        processWeight(weightResult)
        if (weightResult.notAbleUseCards()) return EmptyWeightResult

        val weightResultByFindStrategy = processFindStrategy(weightResult)
        if (weightResultByFindStrategy != EmptyWeightResult) return weightResultByFindStrategy

        findBestCombination(weightResult)
        return weightResult
    }

    fun findBestCombination(weightResult: EndWeightResult): EndWeightResult {
        if (weightResult.isLessCost()) return weightResult
        weightResult.findBestCombination()
        return weightResult
    }

    private fun processFindStrategy(weightResult: EndWeightResult): WeightResult {
        val firstCard = weightResult.canUseCardsByHandler.first()
        val findStrategy = firstCard.findStrategy
        return findStrategy?.find(weightResult, this) ?: run { EmptyWeightResult }
    }











    /**
     * 发现策略
     */
    fun executeDiscoverChooseCard(vararg cards: Card): Int{
        var maxIndex = 0
        var maxWeight = 0.0
        for(i in cards.indices){
            val comboCard = warManage.parseComboCard(cards[i])
            discoverWeightHandlers.forEach {
                it.cardWeight(comboCard)
            }
            val extWeight = pointToDouble(comboCard.basePowerWeight)
            if (extWeight != 0) {
                myLog.info { "id:${comboCard.cardId()},额外权重:$extWeight,也就是weight小数部分" }
            }
            val finalWeight = comboCard.powerWeight + extWeight
            if (finalWeight > maxWeight) {
                maxWeight = finalWeight
                maxIndex = i
            }
        }
        val maxWeightCard = cards[maxIndex]
        myLog.info { "发现权最大值:id:${maxWeightCard.cardId},名字:${maxWeightCard.entityName}的发现权重:$maxWeight,选择下标:$maxIndex" }

        return maxIndex

    }

    fun pointToDouble(number: Double): Int {
        val decimalStr = "%.3f".format(number)  // 使用足够精度格式化
        val decimalIndex = decimalStr.indexOf('.')

        if (decimalIndex == -1) return 0

        val decimalPart = decimalStr.substring(decimalIndex + 1)
        // 移除开头的零并转换为整数
        return decimalPart.trimStart('0').toIntOrNull() ?: 0


    }



}