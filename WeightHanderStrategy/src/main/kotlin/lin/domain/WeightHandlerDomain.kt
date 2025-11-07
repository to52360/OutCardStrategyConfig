package lin.domain

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import lin.bean.ComboCard
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.domain.result.EmptyWeightResult
import lin.domain.result.EndWeightResult
import lin.domain.result.WeightResult
import lin.myLog
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.my.base.getCost
import lin.weightHandler.DiscoverWeightHandler
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import org.koin.core.component.KoinComponent


class WeightHandlerDomain(val warManage: MyWarManage) : KoinComponent {
    private val weightHandlers: MutableList<WeightHandler> = mutableListOf()
    private val discoverWeightHandlers: MutableList<DiscoverWeightHandler> = mutableListOf()

    init {
        try {
            val infos = warManage.infoMap
            val cardWeightInfos =  infos.values.toList()
            val services = ServiceLoaderUtils.loadServicesByMutable(WeightHandler::class.java, weightHandlers)

            services.sortBy {
                //按ai的说法会语义多重,实践看看有什么后果
                if(it is InitHandler) {
                    it.init(cardWeightInfos)
                }


                warManage.registerLifecycle(it)


                //todo 存在一个问题没法单独扩展发现策略
                if (it is DiscoverWeightHandler) {
                    discoverWeightHandlers.add(it)
                }
                it.priority()
            }


        } catch (e: Exception) {
            e.printStackTrace()
            myLog.error(e) { "测试化失败" }
            throw e
        }


    }

    /**
     * 调用权重规则
     */
    private fun processWeight(weightResult: EndWeightResult) {
        weightResult.canUseCards.forEach { comboCard ->
            for (weightHandler in weightHandlers) {
                val calWeight = weightHandler.cardWeightCompute(comboCard, warManage)
                if (calWeight != NotWeight) {
                    //不使用结束循环
                    if (calWeight == UnUseWeight) {
                        comboCard.unUse()
                        break
                    }
                    comboCard.addWeight(calWeight)
                }

            }
            weightResult.processWeightAfter(comboCard)
        }
    }

    /**
     * 查找组合
     * 存在循环调用
     */
    fun findCombination(
        cost: Int = warManage.getCost(),
        canUseCardsByCost: List<ComboCard> = warManage.canUseCards
    ): WeightResult {
        myLog.info { "执行查组组合" }
        if (canUseCardsByCost.isEmpty()) return EmptyWeightResult
        val weightResult = EndWeightResult(canUseCardsByCost, cost)
        processWeight(weightResult)
        if (weightResult.notAbleUseCards()) return weightResult

        findBestCombination(weightResult)
        return weightResult
    }

    /**
     * 查找最好的组合
     */
    fun findBestCombination(weightResult: EndWeightResult): EndWeightResult {
        weightResult.findBestCombination()
        return weightResult
    }




    /**
     * 发现策略
     */
    fun executeDiscoverChooseCard(vararg cards: Card): Int{
        var maxIndex = 0
        var maxWeight = NotWeight
        for(i in cards.indices){
            val comboCard = warManage.parseComboCard(cards[i])
            var baseWeight = NotWeight
            discoverWeightHandlers.forEach {
                baseWeight += it.cardWeight(comboCard)
            }
            val extWeight = pointToDouble(comboCard.basePowerWeight)
            if (extWeight != 0) {
                myLog.info { "id:${comboCard.cardId()},额外权重:$extWeight,也就是weight小数部分" }
            }
            val finalWeight = baseWeight + extWeight
            if (finalWeight > maxWeight) {
                maxWeight = finalWeight
                maxIndex = i
            }
        }
        val maxWeightCard = cards[maxIndex]
        myLog.info { "发现权最大值:id:${maxWeightCard.cardId},名字:${maxWeightCard.entityName}的发现权重:$maxWeight,选择下标:$maxIndex" }

        return maxIndex

    }

    private fun pointToDouble(number: Double): Int {
        val decimalStr = "%.3f".format(number)  // 使用足够精度格式化
        val decimalIndex = decimalStr.indexOf('.')

        if (decimalIndex == -1) return 0

        val decimalPart = decimalStr.substring(decimalIndex + 1)
        // 移除开头的零并转换为整数
        return decimalPart.trimStart('0').toIntOrNull() ?: 0


    }



}