package lin.lifecycle

interface LifecycleRegister {
    fun register(lifecycle: Any)
    fun logouts(anyList: List<Any>)
}