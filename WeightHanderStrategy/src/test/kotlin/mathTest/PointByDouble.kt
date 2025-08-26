package mathTest

import kotlin.test.Test

class PointByDouble {
    @Test
    fun test1() {
        println(pointToDouble(-11.03))
    }

    fun pointToDouble(number: Double): Int {
        val decimalStr = "%.3f".format(number)  // 使用足够精度格式化
        val decimalIndex = decimalStr.indexOf('.')

        if (decimalIndex == -1) return 0

        val decimalPart = decimalStr.substring(decimalIndex + 1)
        // 移除末尾的零并转换为整数
        return decimalPart.trimStart('0').toIntOrNull() ?: 0


    }

    @Test
    fun test2() {
        println(calProb(1, 2))
    }

    fun calProb(num: Int, size: Int): Double {
        val groupWeight = 10.0
        if (num == 0) return -groupWeight
        val prob = num.toFloat() / size
        if (prob < -0.5F) return -groupWeight * prob
        return prob * groupWeight
    }
}