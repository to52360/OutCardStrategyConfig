import com.alibaba.fastjson.JSON
import kotlin.test.Test

class 扩展方法Test {
    @Test
    fun 扩展方法于函数(){
        val b :B = BImpl()
        println(b.test())
        val json = JSON.toJSONString(b)
         JSON.parseObject(json,b.clazz)


    }

}
 abstract class A<T>(open val string: String){
    abstract  fun copy():T

}
data class AImpl(override val string:String): A<AImpl>(string) {
    override fun copy(): AImpl {
        TODO("Not yet implemented")
    }
}
abstract class  B{
    val clazz = this::class.java

}
class BImpl : B()
fun B.test():String{
    return "ce"
}
