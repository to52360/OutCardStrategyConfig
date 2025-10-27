package lin.serviceLoader.weightRule.utils.war


fun WarStatus.excessDamage(): Int {
    if (rivalCards.isEmpty()) return 0
    if (meTaunt.isEmpty()) return rivalSumAtc

    val tauntNum = meTaunt.size
    val rivalNum = rivalCards.size
    // 1. 如果嘲讽数量 ≥ 敌方数量：全部攻击被挡
    if (tauntNum >= rivalNum) {
        return 0
    }
    val meTauntBlood = meTaunt.sumOf { it.blood() }
    if (meTauntBlood >= rivalSumAtc) return 0

    val sortedRivalAtc = rivalCards.map { it.atc }.sortedDescending()
    val attackingTauntAtc = sortedRivalAtc.take(tauntNum).sum()


    val canBlock = meTauntBlood >= attackingTauntAtc //表示能承受最高攻击力的攻击
    if (canBlock) {
        val directDamage = sortedRivalAtc.drop(tauntNum).sum()           // 打脸的溢出伤害
        return directDamage
    } else {
        //采取悲观算法
        return calculateOverflowDamage()
    }


}

/**
 * 悲观算法
 */
private fun WarStatus.calculateOverflowDamage(): Int {


    // 嘲讽血量升序（少血先扛）
    val tauntHp = meTaunt.map { it.blood() }.toMutableList()
    tauntHp.sort()
    // 敌方攻击降序（低攻先打）
    val attacks = rivalCards.map { it.atc }.sorted()

    var overflow = 0
    var tauntIndex = 0 // 当前分配攻击的嘲讽索引

    for (atk in attacks) {
        // 如果当前嘲讽已死，找下一个
        while (tauntIndex < tauntHp.size && tauntHp[tauntIndex] <= 0) {
            tauntIndex++
        }

        if (tauntIndex >= tauntHp.size) {
            // 无存活嘲讽，全部溢出
            overflow += atk
        } else {
            // 攻击当前嘲讽
            tauntHp[tauntIndex] -= atk
            // 注意：不立即移动 tauntIndex，因为可能还能继续扛（比如 5血挨3攻还剩2）
        }
    }

    return overflow
}