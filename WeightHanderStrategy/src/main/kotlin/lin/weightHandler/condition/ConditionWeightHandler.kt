package lin.weightHandler.condition


import lin.bean.CardWeightInfo
import lin.bean.ComboCard
import lin.domain.MyWarManage
import lin.serviceLoader.weightRule.WeightCondition
import lin.weightHandler.InitHandler
import lin.weightHandler.WeightHandler
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.core.component.KoinComponent
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
    override fun cardWeightProcess(callCard: ComboCard, warManage: MyWarManage) {
        callCard.weightRules?.forEach {
            it.calculateSetWeight(callCard, warManage)
        }
    }

    /**
     * 初始化,为卡牌冗余条件计算
     */
    override fun init(infos: List<CardWeightInfo>) {
        //条件组信息
        val weightGroupInfos: List<ConditionGroup> = loadConfig() ?: return


        val init = ConditionHandlerInit(infos)
        //遍历解析组信息
        weightGroupInfos.forEach { conditionGroup ->
            init.parseConditionGroup(conditionGroup)
        }

    }

}