package delegate

interface GroupWeight {
    var groupWeight: Double
}

interface WeightRule {
    fun calculate(): Double
}

// 复合接口
interface WeightRuleTest : WeightRule, GroupWeight

// 委托类，提供默认实现
class GroupWeightDelegate : GroupWeight {
    override var groupWeight: Double = 0.0
}

// 实现类，使用 by 委托实现接口
class WeightRuleTestImpl : WeightRuleTest,GroupWeight by GroupWeightDelegate() {
    // 实现 WeightRule 的方法
    override fun calculate(): Double {
        return groupWeight * 2
    }
}