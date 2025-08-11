package lin.utils.collection

class NullableListDelegate<T> {
    private var list: MutableList<T>? = null

    val value: List<T>
        get() = list ?: emptyList()

    fun add(item: T) {
        if (list == null) {
            list = mutableListOf(item)
        } else {
            list?.add(item)
        }
    }

    fun clear() {
        list = null
    }

    fun getMutableList(): MutableList<T>? {
        return list
    }
}