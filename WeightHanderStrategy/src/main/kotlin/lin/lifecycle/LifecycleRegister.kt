package lin.lifecycle

interface LifecycleRegister {
    fun register(any: Any)
    fun logout(anyList: List<Any>)
}