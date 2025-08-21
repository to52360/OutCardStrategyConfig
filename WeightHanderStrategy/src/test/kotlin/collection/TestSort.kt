package collection

import kotlin.test.Test

data class Card1(val entityId: Int)
data class ComboCard1(val card: Card1, val changeWeight: Int)
class TestSort {
    @Test
    fun test1() {
        val testData = listOf(
            ComboCard1(Card1(100), 10),
            ComboCard1(Card1(200), 20),
            ComboCard1(Card1(150), 10),  // 与第一个元素 changeWeight 相同
            ComboCard1(Card1(300), 30),
            ComboCard1(Card1(250), 20)   // 与第二个元素 changeWeight 相同
        )
        // 应用排序逻辑
        val sortedData = testData.sortedWith(
            compareByDescending<ComboCard1> { it.changeWeight }
                .thenByDescending { it.card.entityId }
        )

        // 打印排序结果
        println("排序后的结果：")
        sortedData.forEach { comboCard ->
            println("changeWeight: ${comboCard.changeWeight}, entityId: ${comboCard.card.entityId}")
        }
    }
}