


import kotlin.math.absoluteValue
import kotlin.test.Test

class IdeaTest {
    @Test
    fun testDouble(){
        val number: Double = -123.45

        // 判断正负
        when {
            number > 0 -> println("$number 是正数")
            number < 0 -> println("$number 是负数")
            else -> println("$number 是零")
        }

        // 获取小数点后一位
        val firstDecimalDigit = ((number * 10).toInt() % 10).absoluteValue
        println("小数点后第一位数字是: $firstDecimalDigit")
        val int  = number.toInt().absoluteValue
        println(int)
        // 或者使用字符串方法
        val decimalStr = number.toString().split('.')
        if (decimalStr.size > 1) {
            println("小数点后第一位数字(字符串方法)是: ${decimalStr[1][0]}")
        }
    }

    @Test
    fun test2() {
        println(javaClass.simpleName)
    }



}