package lin.serviceLoader.findCombo


import lin.bean.ComboCard
import lin.bean.addSafe
import lin.domain.MyWarManage
import lin.domain.WarInfo
import lin.domain.context.UseSkillWeight
import lin.domain.result.*
import lin.domain.strategy.FindComboStrategy.Companion.SKILL_PRIORITY
import lin.domain.strategy.FindPlanner
import lin.domain.strategy.UseAfterStrategy
import lin.domain.strategy.UseStrategyUtils
import lin.warExt.my.base.getCost
import lin.warExt.my.base.getPower


class SkillFindStrategy : AbsFindStrategy(findRule = { false }), UseAfterStrategy {
    private val registerId = this.javaClass.simpleName
    private var skillComboCard: ComboCard? = null
    private var isUsedSkill = false

    //负权重不参与计算
    private var isNotCalculate = true

    /**
     * 没回合更新一次技能信息
     * @return 是否使用技能
     */
    fun processSkill(warManage: MyWarManage): Boolean {
        fun createSkill() {
            skillComboCard = warManage.getPower()?.let {
                val skill = warManage.parseComboCard(it)
                skill.addWeight(UseSkillWeight)
                extCost = -skill.cost()
                extWeight = skill.powerWeight
                skill.useAfterStrategy.addSafe(this)
                isNotCalculate = !skill.canUse()
                skill
            } ?: run { throw IllegalArgumentException("没有英雄技能") }
        }
        if (warManage.roundExecuteOnce(registerId)) {
            isUsedSkill = false
            skillComboCard?.let {
                val skill = warManage.getPower()
                if (it.card != skill) {
                    createSkill()
                }
            } ?: run {
                createSkill()
            }
        }
        return isUsedSkill


    }

    override fun priority() = SKILL_PRIORITY


    override fun isExecute(findPlanner: FindPlanner): Boolean {
        val warManage = findPlanner.warManage
        return processSkill(warManage) || skillComboCard?.let { warManage.getCost() < it.cost() } ?: true
    }


    override fun emptyResultAction(findPlanner: FindPlanner) {
        isUsedSkill = true
        skillComboCard?.let {
            findPlanner.warManage.tryUseCard(it)
        } ?: throw IllegalStateException("没有技能信息")

    }

    override fun find(findPlanner: FindPlanner): CmdPlanner {
        if (isExecute(findPlanner)) return ContinuePlanner
        emptyResultAction(findPlanner) //直接使用技能
        return EmptyWeightResult.toPlanner()
    }

    override fun find(findPlanner: FindPlanner, weightResult: WeightResult): WeightResult {
        if (isExecute(findPlanner)) return weightResult
        if (isNotCalculate) {
            processIsNotCalculate(findPlanner, weightResult)
            return weightResult
        } else {
            return processResult(findPlanner, weightResult)
        }
    }

    private fun processIsNotCalculate(findPlanner: FindPlanner, weightResult: WeightResult) {
        when (weightResult) {
            is EndWeightResult -> skillComboCard?.let { weightResult.addUseCard(it) }
                ?: throw IllegalStateException("没有技能信息")

            is EmptyWeightResult -> emptyResultAction(findPlanner)
        }
    }

    override fun resultAction(findPlanner: FindPlanner, endWeightResult: EndWeightResult) {
        skillComboCard?.let { endWeightResult.addUseCard(it) } ?: throw IllegalStateException("没有技能信息")
    }

    override fun afterExtAction(
        comboCard: ComboCard,
        useStrategyUtils: UseStrategyUtils,
        warInfo: WarInfo
    ) {
        isUsedSkill = true
    }
}
