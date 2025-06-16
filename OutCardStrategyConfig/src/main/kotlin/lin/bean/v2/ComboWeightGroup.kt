package lin.bean.v2

import lin.strategy.ComboRole

class ComboWeightGroup(
    val groupId: Int,
    val comboRoleByRelatedGroupIn :Map<String,ComboRole>, //key为relatedGroup,这些关联组扮演的角色
    val comboRole : ComboRole,// 扮演的角色
    val outCardStrategyId : String, //打出策略,依赖关联组 ,辅助类:核心卡没上手,依赖项:在手牌
    val basePriority: Int?=null //基础优先度
) {
}