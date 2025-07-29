package lin.domain

import club.xiaojiawei.bean.Card
import lin.bean.ComboCard
import lin.myLog
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.bean.UseType
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.CardWeightHandler
import lin.weightHandler.condition.context.CostWeight

typealias ProcessWeightByCostsFun = (WeightHandler, ComboCard) -> Unit
class WeightHandlerDomain(private val warManage: MyWarManage) {
    private val weightHandlers: List<WeightHandler>
    private val cardWeightHandlers: List<CardWeightHandler>

    private var canUseCardsByHandler:List<ComboCard> = emptyList()
    val readCanUseCardsByHandler: List<ComboCard>
        get() = canUseCardsByHandler
    init {

        try {
            val infos = warManage.infoMap
            myLog.info { "权重信息的id集合:${infos.keys}" }
            val cardWeightInfos =  infos.values.toList()
            val services =ServiceLoaderUtils.loadServices(WeightHandler::class.java)
            myLog.info { "加载到的权重处理器的类名:${services.joinToString(","){it::class.simpleName.toString()}}" }
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
            }
            return canUseCardsByHandler
        }
    }
    /**
     *
     */
     fun executeWeightProcess(canUseCardsByCost:List<ComboCard>)  {
        this.canUseCardsByHandler = processWeightEnvironment(canUseCardsByCost) { weightHandler, comboCard ->
            weightHandler.cardWeightProcess(comboCard, warManage)
        }

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
        this.canUseCardsByHandler = weightProcess(canUseCardsByCost)
    }

    /**
     * 查询是否有需要优先执行的卡
     */
    fun findBeforeOrBestCombination():List<ComboCard>{
        if(canUseCardsByHandler.size<2){
            return canUseCardsByHandler
        }
        this.canUseCardsByHandler = canUseCardsByHandler.sortedBy { it.powerWeight }
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
         val sumCost = nowCostCards.sumOf { it.powerWeight }
         myLog.info { "现在费用的权重:${sumCost},成员:$nowCostCards" }


         //todo-future 1.复杂状态关系要不要封装成对象,2.这里存在很多复制数组的操作,性能没问题就不优化
         val expectCostCard = warManage.canUseCardsByCost(expectCost+warManage.getNowCost()).copy(comboCard)
        //todo-future  采用复制性能问题,但是应该不常用,到时再看
         val expectCostCardByWeight = weightProcess(expectCostCard)
         //todo-future 这里还可以提供
         val expectCostCards =findBestCombination(expectCostCardByWeight)


        //todo 超模才用硬币,也可能一直不打硬币情况
        val reduceWeight = expectCost*5.0
        val sumExpectCost = expectCostCard.sumOf { it.powerWeight }-reduceWeight
        myLog.info { "期望费用的权重:${sumExpectCost},成员:$expectCostCards" }


        return if(sumCost>sumExpectCost){
            this.canUseCardsByHandler = nowCostCardsByWeight
            nowCostCards
        }else{
            warManage.useCard(comboCard)//使用
            //todo 没排序
            this.canUseCardsByHandler = expectCostCardByWeight
            expectCostCards
        }

    }


    /**
     * 查询前置优化操作
     */
    private fun findBestCombination(canUseCardsByHandler:List<ComboCard>):List<ComboCard>{
        return if(canUseCardsByHandler.size>1){
            findBestCombination(comboCards=canUseCardsByHandler)
        }else{
            canUseCardsByHandler
        }
    }
    // 3. 定义一个递归函数（回溯）来查找所有可能的组合 ai生成 待验证
    private fun findBestCombination(cost:Int = warManage.getNowCost(),comboCards:List<ComboCard> = canUseCardsByHandler): List<ComboCard> {

        val sumCost = comboCards.sumOf { it.getCost() }
        if(sumCost<cost){//总费用小于可用费用 直接不用组合了
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
            val remainingCost = cost - currentCost

            val penalty = remainingCost * CostWeight
            val effectiveScore = currentWeight - penalty

            // 如果当前组合的有效分超过了已知的最高分，则更新最佳组合
            if (effectiveScore > maxEffectiveScore) {
                maxEffectiveScore = effectiveScore
                bestCombination = currentCombination
            }

            // 从 startIndex 开始遍历，继续添加新的牌来探索更深的组合
            for (i in startIndex until comboCards.size) {
                val card = comboCards[i]
                if (card.useAble() && currentCost + card.getCost() <= cost) {
                    //同组加权 todo 还有同组排序
                    val comboWeight = card.comboAddWeight(currentCombination)
                    findBestCombination(
                        startIndex = i + 1,
                        currentCost = currentCost + card.getCost(),
                        currentWeight = currentWeight + card.powerWeight + comboWeight,
                        currentCombination = currentCombination + card
                    )
                }
            }
        }

        // 4. 启动回溯搜索
        // 初始状态是空组合，从索引0开始
        findBestCombination(0, 0, 0.0, emptyList())
        return bestCombination

    }


    fun executeDiscoverChooseCard(vararg cards: Card): Int{
        var maxIndex = 0
        var maxWeight = 0.0
        for(i in cards.indices){
            val comboCard = warManage.parseComboCard(cards[i])
            cardWeightHandlers.forEach {
                it.cardWeight(comboCard)
            }
            if(comboCard.powerWeight>maxWeight){
                maxWeight = comboCard.powerWeight
                maxIndex = i
            }
        }
        return maxIndex

    }

    /**
     * 存在性能问题,暂时这样了
     */
    fun List<ComboCard>.copy(skipComboCard: ComboCard):List<ComboCard>{
        val comboCards = mutableListOf<ComboCard>()
        forEach {
            if(skipComboCard != it.card){//重写的equals,不知道==起效不
                val comboCard = warManage.parseComboCard(it.card)
                comboCards.add(comboCard)
            }

        }
        return comboCards
    }



}