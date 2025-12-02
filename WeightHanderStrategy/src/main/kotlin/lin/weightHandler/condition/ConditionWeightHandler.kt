package lin.weightHandler.condition


import lin.bean.ComboCard
import lin.bean.cardExt.base.weightRules
import lin.config.ConfigDispatcher
import lin.config.Rules
import lin.config.processMoreConfig
import lin.domain.MyWarManage
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.myLog
import lin.rule.RuleInfoRegister
import lin.serviceLoader.weightRule.WeightCondition
import lin.weightHandler.WeightHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 条件权重处理器
 */
class ConditionWeightHandler : WeightHandler, KoinComponent {

    init {
        val ruleInfoRegister = get<RuleInfoRegister>()
        val ruleMap = ruleInfoRegister.getRules<WeightCondition>()
        val configDispatcher = get<ConfigDispatcher>()
        ruleMap.forEach {
            configDispatcher.processMoreConfig(it.key, Rules(it.value))
        }
    }


    //todo-future 存在魔数
    override fun priority() = 5

    override fun cardWeightCompute(callCard: ComboCard, warManage: MyWarManage): Double {
        var calWeight = NotWeight
        callCard.weightRules()?.let {
            for (weightRule in it) {
                val weight = weightRule.calculateWeight(callCard, warManage)
                if (weight != NotWeight) {
                    myLog.info { "处理id:${weightRule.id()},卡牌id:${callCard.cardId()}的计算权重:${weight},总权重:${callCard.powerWeight + weight}" }
                    if (weight == UnUseWeight) {
                        return weight
                    }
                    calWeight += weight
                }

            }
        }

        return calWeight

    }





}
