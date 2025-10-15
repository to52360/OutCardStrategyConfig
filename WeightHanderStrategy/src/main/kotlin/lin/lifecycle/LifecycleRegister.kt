package lin.lifecycle

interface LifecycleRegister {
    fun register(lifecycle: Any)
    fun logout(anyList: List<Any>)
}