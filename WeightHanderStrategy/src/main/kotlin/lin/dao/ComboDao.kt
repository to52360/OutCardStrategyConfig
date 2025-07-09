package lin.dao

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log

import club.xiaojiawei.strategy.HsRadicalDeckStrategy
import lin.bean.ComboCard

import lin.weightHandler.condition.bean.ComboWeightInfo
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.context.CostWeight
import java.util.*


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
class ComboDao(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>,war: War) {
    //存储转化权重信息
    private val warManage: MyWarManage
    private val weightHandlers:List<WeightHandler>

    //存储策略分组
    init {

        val infoMap: Map<String, ComboWeightInfo> = parse(weightConfigs)
        warManage = MyWarManage(war, infoMap)
        weightHandlers = getWeightHandler()

    }
    //把配置信息转化成上下文信息
    private fun parse(weightConfigs: MutableList<LikeTrie.Entry<CardWeight>>): Map<String, ComboWeightInfo> {

        return   weightConfigs.associateBy(
            keySelector = { it.key }
        ) {weightCard ->
            ComboWeightInfo(weightCard.key,weightCard.value.weight,weightCard.value.powerWeight)
        }
    }

    /**
     * 权重处理器
     */
    private fun getWeightHandler():List<WeightHandler> {
       return  ServiceLoader.load(WeightHandler::class.java).sortedBy {
            it.priority()
        }
    }

    var initResult: Boolean = false


    /**
     * 没有权重信息或者没有匹配对应策略
     */
    private fun hasValidStrategy(): Boolean {
        TODO()
    }

    /**
     * 返回出牌策略,给策略类
     */
    fun getOutCardLambda(): () -> Unit {

      return {executeOnErrorProcess()}
    }

    /**
     * 策略执行环境
     */
    private  inline  fun executeEnvironment(runnable: ()->Unit) {
        try{
            if(warManage.isValid()){
                warManage.activeLocation()
                //重新加载信息
                warManage.reLoad()
                runnable()
                warManage.activeLocation()
            }

        }catch (e:Exception){
            log.error { e.message }
            throw e
        }
    }
    /**
     * 统一错误记录日志处理
     */
    private fun executeOnErrorProcess(){
        executeEnvironment {
            //获取能够打出的卡牌
            val canUseCardsByCost = warManage.getCanUseCardsByCost()
            executeOutCardStrategy(canUseCardsByCost)
        }
    }

    private fun executeOutCardStrategy(canUseCardsByCost:List<ComboCard>) {

        if(canUseCardsByCost.isEmpty()){
            return
        }else{
            val canUseCardsByHandler = mutableListOf<ComboCard>()
            //todo-future 可能有性能问题,出了问题再看看
            canUseCardsByCost.forEach {
                weightHandlers.forEach { handler ->handler.cardWeightProcess(it,warManage)}
                //过滤出经过权重处理器能使用的卡牌
                if(it.useAble()) canUseCardsByHandler.add(it)
            }
            if(canUseCardsByHandler.size==1){
                val comboCard = canUseCardsByHandler.first()
                    warManage.useCard(comboCard)
            }else{
                 canUseCardsByHandler.sortBy { it.varPowerWeight }
                //todo-future 这里的判断不合理,可能出现组合比单卡好的情况,看以后的情况
                if (canUseCardsByHandler.first().getCost() == warManage.getNowCost()) {//刚好占满费用不用找了
                    warManage.useCard(canUseCardsByHandler.first())
                    return
                } else if(canUseCardsByHandler.first().isRefresh){//打出刷新 针对改变手牌
                    warManage.useCardAndUpdate(canUseCardsByHandler.first())
                    warManage.refreshComboCards()
                    executeOutCardStrategy(warManage.getCanUseCardsByCost())
                } else {
                    //查找权重最高的组合
                    val bestCombination =  findAndUseCard(canUseCardsByHandler)
                    //使用卡牌
                    useCard(canUseCardsByHandler, bestCombination)

                }
            }
        }


    }



    // 3. 定义一个递归函数（回溯）来查找所有可能的组合 ai生成 待验证
    private fun findAndUseCard(comboCards: List<ComboCard>):List<ComboCard>  {
        val cost = warManage.getNowCost() // 当前费用


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
        ){
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
                if (card.useAble()&&currentCost + card.getCost() <= cost) {
                    //同组加权
                    val comboWeight = card.comboAddWeight(currentCombination)
                    findBestCombination(
                        startIndex = i + 1,
                        currentCost = currentCost + card.getCost(),
                        currentWeight = currentWeight + card.varPowerWeight+comboWeight,
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
    private fun useCard(canUseCardsByHandler:List<ComboCard>, bestCombination:List<ComboCard>) {
        // 5. 执行找到的最佳出牌组合
        if (bestCombination.isNotEmpty()) {
            val finalCost = bestCombination.sumOf { it.getCost() }

            log.info {
                val finalWeight = bestCombination.sumOf { it.varPowerWeight }
                val msg = "找到最优出牌组合 (总费用: $finalCost, 总权重: $finalWeight): ${bestCombination.map { it.card.cardId }}"
                println(msg)
                msg
            }


            val expectCost = warManage.getNowCost()-finalCost
            bestCombination.forEach { warManage.useCard(it) }
            //todo-future 直接遍历使用
            if (warManage.getNowCost()>expectCost) {//说明有些牌没打出去
                val moreTryCard = canUseCardsByHandler- bestCombination.toSet()
                if(moreTryCard.isNotEmpty()){
                    for(card in moreTryCard){
                        if(warManage.getNowCost()>=card.getCost() ) {
                            warManage.useCard(card)
                            if (!warManage.hasCost()) break
                        }
                    }
                }


            }
        }
    }


}

private val defaultStrategy: HsRadicalDeckStrategy by lazy {
    HsRadicalDeckStrategy()
}

//默认策略
val defaultOutCardLambda: () -> Unit = {
    defaultStrategy.executeOutCard()
}

