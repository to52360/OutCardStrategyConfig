package lin.domain.context

//一费10点权重
const val CostWeight = 10.0
const val HalfCostWeight = CostWeight / 2
const val NotWeight = 0.0
const val BaseWeight = 1.0
const val OrderWeight = 1.0
const val UnUseWeight = -100.0
const val UseSkillWeight = -10.0


//每个人都不同 等待动作的操作 例如等待发现的动作
const val DealyActionTime: Long = 5000

//每个人都不同 等待打出动画时间
const val AwaitAnimationTime: Long = 1000

const val ThreeAnimationTime: Long = AwaitAnimationTime * 3

