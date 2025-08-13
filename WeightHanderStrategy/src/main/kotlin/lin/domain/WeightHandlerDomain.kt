package lin.domain

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.lifecycle.LifecycleRegister
import lin.myLog

import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.base.getNowCost
import lin.weightHandler.DiscoverWeightHandler
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.math.absoluteValue


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
            discoverWeightHandlers.forEach {
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