package lin.dao

import lin.bean.ComboCard
import lin.myLog
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.bean.CardWeightInfo
import lin.bean.UseType
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.condition.context.BaseWeight
import lin.weightHandler.condition.context.CostWeight
import java.util.*
typealias UseCardsByCostsFun = (WeightHandler,ComboCard) -> Unit
class WeightHandlerDao(private val warManage: MyWarManage) {
    private val weightHandlers: List<WeightHandler>
    var canUseCardsByHandler:List<ComboCard> = emptyList()
    init {

        try {
            weightHandlers = getWeightHandler(warManage.infoMap)
        } catch (e: Exception) {
            e.printStackTrace()
            myLog.error(e) { "测试化失败" }
            throw e
        }


    }
    /**
     * 权重处理器
     */
    private fun getWeightHandler(infos:Map<String, CardWeightInfo>): List<WeightHandler> {
        myLog.info { "权重信息的id集合:${infos.keys}" }
        val cardWeightInfos =  infos.values.toList()
        val services =ServiceLoaderUtils.loadServices(WeightHandler::class.java)
        myLog.info { "加载到的权重处理器的类名:${services.joinToString(","){it::class.simpleName.toString()}}" }
        return services.sortedBy {
            //按ai的说法会语义多重,实践看看有什么后果
            if(it is InitHandler) {
                it.init(cardWeightInfos)
            }
            it.priority()
        }
    }

    private inline fun cardsByCostsEnvironment(canUseCardsByCost:List<ComboCard>, useFunction:UseCardsByCostsFun):List<ComboCard> {

        if (canUseCardsByCost.isEmpty()) {
           return emptyList()
        } else {
            val canUseCardsByHandler = mutableListOf<ComboCard>()
            //todo-future 可能有性能问题,项目初期不考虑太多东西 出了问题再看看
            canUseCardsByCost.forEach {comboCard->
                //todo-future 复合权重暂时这样处理,暂时不使用复杂标记策略(处理过的就不处理了)和权重处理链
                weightHandlers.forEach { useFunction(it,comboCard) }
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
        this.canUseCardsByHandler = cardsByCostsEnvironment(canUseCardsByCost) { weightHandler, comboCard ->
            weightHandler.cardWeightProcess(comboCard, warManage)
        }

    }
    private fun weightProcess(canUseCardsByCost:List<ComboCard>) :List<ComboCard> {
        //todo 暂不使用handChaWeightProcess清空权重使用cardWeightProcess重新计算,先测试,看handChaWeightProcess怎么改进
        canUseCardsByCost.forEach { it.varPowerWeight= BaseWeight }
        return cardsByCostsEnvironment(canUseCardsByCost) { weightHandler, comboCard ->
            weightHandler.cardWeightProcess(comboCard, warManage)
        }

    }
    /**
     * 手牌变更重新计算权重
     * todo 这个方案也有问题,如果已满足就不计算,会产生额外的策略
     */
     fun executeHandChaWeightProcess(canUseCardsByCost:List<ComboCard>) {

        this.canUseCardsByHandler = weightProcess(canUseCardsByCost)
    }

    /**
     * 查询是否有需要优先执行的卡
     */
    fun findBeforeOrBestCombination():List<ComboCard>{
        if(canUseCardsByHandler.size<2){
            return canUseCardsByHandler
        }
        this.canUseCardsByHandler = canUseCardsByHandler.sortedBy { it.varPowerWeight }
        //需要组合之前,有特殊操作
        if(canUseCardsByHandler.first().useStrategy.useType== UseType.BEFORE){
            return listOf(canUseCardsByHandler.first())
        }
        return findBestCombination()
    }
    fun findBestCombination(comboCard: ComboCard,expectCost:Int):List<ComboCard>{
         val nowCostCardsByWeight  = this.canUseCardsByHandler-comboCard
         val  nowCostCards =  findBestCombination(nowCostCardsByWeight)
         val sumCost = nowCostCards.sumOf { it.varPowerWeight }
         myLog.info { "现在费用的权重:${sumCost},成员:$nowCostCards" }


         //todo-future 复杂状态关系要不要封装成对象,这里存在很多复制数组的操作,性能没问题就不优化
         val expectCostCard = warManage.canUseCardsByCost(expectCost+warManage.getNowCost()) -comboCard
         val expectCostCardByWeight = weightProcess(expectCostCard)
         //todo-future 这里还可以提供
         val bestCombination =findBestCombination(expectCostCardByWeight)


        //todo 超模才用硬币,也可能一直不打硬币情况
        val reduceWeight = expectCost*5.0
        val sumExpectCost = expectCostCard.sumOf { it.varPowerWeight }-reduceWeight
        myLog.info { "期望费用的权重:${sumExpectCost},成员:$bestCombination" }


        return if(sumCost>sumExpectCost){
            this.canUseCardsByHandler = nowCostCardsByWeight
            nowCostCards
        }else{
            warManage.useCard(comboCard)//使用
            //todo 没排序
            this.canUseCardsByHandler = expectCostCardByWeight
            bestCombination
        }

    }
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
                        currentWeight = currentWeight + card.varPowerWeight + comboWeight,
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



}