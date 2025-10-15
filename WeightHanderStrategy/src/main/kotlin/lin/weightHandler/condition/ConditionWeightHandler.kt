package lin.weightHandler.condition


import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.config.ConfigDispatcher
import lin.domain.MyWarManage
import lin.domain.context.NotWeight
import lin.domain.context.UnUseWeight
import lin.myLog
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * 条件权重处理器
 */
class ConditionWeightHandler : WeightHandler, InitHandler, KoinComponent {


    /**
     * todo 没有配置信息ui 暂时硬编码 获取配置
     */
    private fun loadConfig(): List<ConditionGroup>? {
        val groupStrategyDao: GroupStrategyDao by inject()
        return groupStrategyDao.getAll()
    }

    //todo-future 存在魔数
    override fun priority() = 5
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage): Double {
        var calWeight = NotWeight
        callCard.weightRules?.run {
            for (weightRule in this) {
                val weight = weightRule.calculateWeight(callCard, warManage)
                if (weight != NotWeight) {
                    myLog.info { "处理id:${weightRule.id()},卡牌id:${callCard.cardId()}的计算权重:${weight},id:${callCard.cardId()},总权重:${callCard.powerWeight + weight}" }
                    if (weight == UnUseWeight) {
                        return weight
                    }
                    calWeight += weight
                }

            }

        }
        return calWeight
    }

    /**
     * 初始化,为卡牌冗余条件计算
     */
    override fun init(infos: List<CardWeightInfo>) {
        //条件组信息
        val weightGroupInfos: List<ConditionGroup> = loadConfig() ?: return


        val init = ConditionHandlerInit(infos, get<ConfigDispatcher>())
        //遍历解析组信息
        weightGroupInfos.forEach { conditionGroup ->
            init.parseConditionGroup(conditionGroup)
        }

    }

}