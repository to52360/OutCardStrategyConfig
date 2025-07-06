package lin.dao.temp

import kotlin.math.absoluteValue

object DoubleSplitUtils {
     fun decimalStr(number:Double) = number.toString().split(".").getOrElse(1) { "0" }
     fun decimalInt(number:Double) = ((number - number.toInt()) * 1000).toInt().absoluteValue
}