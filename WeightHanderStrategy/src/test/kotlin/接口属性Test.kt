import kotlin.test.Test

class 接口属性Test {
    @Test
    fun test() {



    }
}

interface MyInterfaceWithDefault {
    val id: String
        get() = "Default ID" // 默认 getter 实现

    var count: Int
        get() = 0 // 默认 getter 实现
        set(value) { // 默认 setter 实现
            println("Setting count to $value")
        }
}

class MyConcreteClass : MyInterfaceWithDefault {
    // 可以选择不重写 id，使用默认实现
    // 可以选择重写 count
    override var count: Int = 100 // 重写 count，提供自己的实现
}

/**
 * 行不通,虽然语法没有问题,但是没有重写不能操作
 */
class AnotherConcreteClass : MyInterfaceWithDefault {
/*    fun setCount(count: Int) {
        this.count = count
    }*/
    // 完全使用默认实现，不需要重写任何属性
}