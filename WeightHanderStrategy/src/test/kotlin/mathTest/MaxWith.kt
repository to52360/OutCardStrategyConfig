package mathTest

data class A(val cost: Int, val changeWeight: Double)

fun filterCards(cards: List<A>): List<A> {
    // 正确的保留逻辑
    val retained = cards.filter { card ->
        if (card.cost < 3) {
            card.changeWeight >= 0
        } else {
            card.changeWeight > 0
        }
    }

    // 如果全部都被保留了，需要移除一个
    return if (retained.size == cards.size && retained.isNotEmpty()) {
        // 移除规则：cost 最大优先；cost 相同时，changeWeight 最小优先
        // 使用自定义比较器
        val toRemove = retained.maxWithOrNull(
            compareBy<A> { it.cost }
                .thenByDescending { it.changeWeight } // 注意：我们要移除 changeWeight 最小的，所以 maxWith 要反过来
        ) ?: error("Unexpected empty retained list")

        retained - toRemove
    } else {
        retained
    }
}

fun main() {
    val cards = listOf(
        A(1, 0.0),   // cost<3, weight=0 → 保留
        A(2, 0.0),   // 保留
        A(1, 2.0),   // 保留
        A(2, 1.0)    // cost>=3, weight>0 → 保留
    )
    println(filterCards(cards))


}