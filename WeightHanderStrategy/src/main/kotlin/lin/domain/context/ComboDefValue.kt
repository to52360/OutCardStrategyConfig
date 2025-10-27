package lin.domain.context

//表示一费5点权重,可以在打不满费用时打出
const val CostWeight = 5.0
const val MaxCostWeight = 10.0
const val NotWeight = 0.0
const val BaseWeight = 1.0
const val OrderWeight = 1.0
const val UnUseWeight = -100.0
const val UseSkillWeight = -7.0


//每个人都不同 等待动作的操作 例如等待发现的动作
const val AwaitAnimationTime: Long = 1000
const val UseAnimationTime: Long = 500
const val ChangeAnimationTime: Long = 2500
const val FourAnimationTime: Long = AwaitAnimationTime * 4






