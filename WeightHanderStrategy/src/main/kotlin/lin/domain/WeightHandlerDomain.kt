package lin.domain

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.bean.UseType
import lin.myLog
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.warExt.base.getNowCost
import lin.weightHandler.CardWeightHandler
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.context.CostWeight

typealias ProcessWeightByCostsFun = (WeightHandler, ComboCard) -> Unit
class WeightHandlerDomain(private val warManage: MyWarManage) {
    private val weightHandlers: List<WeightHandler>
    private val cardWeightHandlers: List<CardWeightHandler>
    var unUseCards = sortedSetOf<ComboCard>(comparator = compareByDescending { it.powerWeight })
        private set

    private var canUseCardsByHandler:List<ComboCard> = emptyList()
    val readCanUseCardsByHandler: List<ComboCard>
        get() = canUseCardsByHandler

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

    fun clean() {
        unUseCards = sortedSetOf<ComboCard>(comparator = compareByDescending { it.powerWeight })
    }

    /**
     * 权重执行环境,为了优化留下扩展
     */
    private inline fun processWeightEnvironment(canUseCardsByCost:List<ComboCard>, processWeightByCostsFun: ProcessWeightByCostsFun):List<ComboCard> {

        if (canUseCardsByCost.isEmpty()) {
           return emptyList()
        } else {
            val canUseCardsByHandler = mutableListOf<ComboCard>()
            //todo-future 可能有性能问题,项目初期不考虑太多东西 出了问题再看看
            canUseCardsByCost.forEach {comboCard->
                //todo-future 复合权重暂时这样处理,暂时不使用复杂标记策略(处理过的就不处理了)和权重处理链
                weightHandlers.forEach { processWeightByCostsFun(it,comboCard) }
                //过滤出经过权重处理器能使用的卡牌
                if (comboCard.useAble()) canUseCardsByHandler.add(comboCard)
                else unUseCards.add(comboCard)
            }
            return canUseCardsByHandler
        }
    }
    /**
     *
     */
     fun executeWeightProcess(canUseCardsByCost:List<ComboCard>)  {
        this.canUseCardsByHandler = weightProcess(canUseCardsByCost)

    }

    /**
     *
     *
     */
    private fun weightProcess(canUseCardsByCost:List<ComboCard>) :List<ComboCard> {
        return processWeightEnvironment(canUseCardsByCost) { weightHandler, comboCard ->
            weightHandler.cardWeightProcess(comboCard, warManage)
        }

    }
    /**
     * 重新计算,手牌变化的策略
     * todo 这个方案也有问题,如果已满足就应该不计算,会产生额外计算,但安全,看看性能怎么样,再说
     * todo 清空权重使用cardWeightProcess重新计算,先测试,看handChaWeightProcess怎么改进
     */
     fun executeHandChaWeightProcess(canUseCardsByCost:List<ComboCard>) {
        warManage.cleanWeight()
        executeWeightProcess(canUseCardsByCost)
    }

    /**
     * 查询是否有需要优先执行的卡
     */
    fun findBeforeOrBestCombination():List<ComboCard>{
        if(canUseCardsByHandler.size<2){
            return canUseCardsByHandler
        }
        this.canUseCardsByHandler = canUseCardsByHandler.sortedByDescending { it.powerWeight }
        //需要组合之前,有特殊操作
        if(canUseCardsByHandler.first().useStrategy.useType== UseType.BEFORE){
            return listOf(canUseCardsByHandler.first())
        }
        return findBestCombination()
    }

    /**
     * 增加额外费用处理,预期和现在对比
     * todo-future 存在复制集合操作,可优化
     */
    fun findBestCombinationByExpectCost(comboCard: ComboCard, expectCost:Int):List<ComboCard>{
         val nowCostCardsByWeight  = this.canUseCardsByHandler-comboCard
         val  nowCostCards =  findBestCombination(nowCostCardsByWeight)
        val nowWeight = nowCostCards.sumOf { it.powerWeight }
        myLog.info { "现在费用的权重:${nowWeight},成员:$nowCostCards" }


         //todo-future 1.复杂状态关系要不要封装成对象,2.这里存在很多复制数组的操作,性能没问题就不优化

        val expectCostCard = warManage.canUseCardsByCost(expectCost).copy(comboCard)
        //todo-future  采用复制性能问题,但是应该不常用,到时再看
         val expectCostCardByWeight = weightProcess(expectCostCard)
         //todo-future 这里还可以提供
        val expectCostCards = findBestCombination(expectCostCardByWeight, expectCost)


        //todo 超模才用硬币,也可能一直不打硬币情况
        val reduceWeight = expectCost*5.0
        val sumExpectWeight = expectCostCard.sumOf { it.powerWeight } - reduceWeight
        myLog.info { "期望费用的权重:${sumExpectWeight},成员:$expectCostCards" }


        return if (nowWeight > sumExpectWeight) {
            this.canUseCardsByHandler = nowCostCardsByWeight
            nowCostCards
        }else{
            warManage.useCardAndRemove(comboCard)//使用增加费用
            //todo 没排序
            this.canUseCardsByHandler = expectCostCardByWeight
            expectCostCards
        }

    }


    /**
     * 查询前置优化操作
     */
    private fun findBestCombination(
        canUseCardsByHandler: List<ComboCard>,
        cost: Int = warManage.getNowCost()
    ): List<ComboCard> {
        return if(canUseCardsByHandler.size>1){
            findBestCombination(cost, comboCards = canUseCardsByHandler)
        }else{
            canUseCardsByHandler
        }
    }
    // 3. 定义一个递归函数（回溯）来查找所有可能的组合 ai生成 待验证
    private fun findBestCombination(useAbleCost:Int = warManage.getNowCost(), comboCards:List<ComboCard> = canUseCardsByHandler): List<ComboCard> {

        val sumCost = comboCards.sumOf { it.getCost() }
        if(sumCost<useAbleCost){//总费用小于可用费用 直接不用组合了
            return comboCards
        }

        // 2. 初始化用于寻找最佳组合的变量
        var bestCombination: List<ComboCard> = emptyList()
        // *** 核心改动 ***: 我们追踪的不再是最大权重，而是最大“有效分”
        // 初始化为一个非常小的值，确保任何合法地出牌都比它好
        var maxEffectiveScore = Double.NEGATIVE_INFINITY

        // 3. 定义一个递归函数（回溯）来查找所有可能的组合
        fun findBestCombination(
            startIndex: Int,
            currentCost: Int,
            currentWeight: Double,
            currentCombination: List<ComboCard>
        ) {
            // *** 核心改动 ***
            // 在每次形成一个有效组合时（包括空组合），都计算其“有效分”
            val remainingCost = useAbleCost - currentCost

            val penalty = remainingCost * CostWeight
            val effectiveScore = currentWeight - penalty

            // 如果当前组合的有效分超过了已知的最高分，则更新最佳组合
            if (effectiveScore > maxEffectiveScore) {
                maxEffectiveScore = effectiveScore
                bestCombination = currentCombination
            }

            // 从 startIndex 开始遍历，继续添加新的牌来探索更深的组合
            for (i in startIndex until comboCards.size) {
                val newCard = comboCards[i]
                if (  newCard.getCost() <= remainingCost) {
                    //同组加权
                    var comboBonus = 0.0
                    currentCombination.forEach { existingCard ->
                        // combo加权
                        comboBonus += existingCard.comboAddWeight(newCard)
                    }

                    findBestCombination(
                        startIndex = i + 1,
                        currentCost = currentCost + newCard.getCost(),
                        currentWeight = currentWeight + newCard.powerWeight + comboBonus ,
                        currentCombination = currentCombination + newCard
                    )
                }
            }
        }

        // 4. 启动回溯搜索
        // 初始状态是空组合，从索引0开始
        findBestCombination(0, 0, 0.0, emptyList())
        return bestCombination

    }

    /**
     * 发现策略
     */
    fun executeDiscoverChooseCard(vararg cards: Card): Int{
        var maxIndex = 0
        var maxWeight = 0.0
        for(i in cards.indices){
            val comboCard = warManage.parseCombo(cards[i])
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
        // 1. 获取小数部分
        val decimalPart = number - number.toInt() // 结果: 0.14159
        if (decimalPart == 0.0) return decimalPart
        // 2. 将小数部分乘以 10^n（n 是你想要保留的小数位数），然后转换为 Int
        // 例如，保留 5 位小数
        val scaleFactor = 100 // 10^5
        val result = decimalPart * scaleFactor
        return result


    }

    /**
     * 存在性能问题,暂时这样了
     */
    fun List<ComboCard>.copy(skipComboCard: ComboCard):List<ComboCard>{
        val comboCards = mutableListOf<ComboCard>()
        forEach {
            if(skipComboCard != it.card){//重写的equals,不知道==起效不
                val comboCard = warManage.parseCombo(it.card)
                comboCards.add(comboCard)
            }

        }
        return comboCards
    }



}