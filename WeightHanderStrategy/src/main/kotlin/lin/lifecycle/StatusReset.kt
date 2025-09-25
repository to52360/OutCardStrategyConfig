package lin.lifecycle

interface RoundExecute {
    fun executeOnce(runnable: () -> Unit): Boolean
}

class StatusReset : RoundExecute {
    val falseKeys = mutableSetOf<String>()

    fun isTrue(key: String): Boolean = key !in falseKeys

    // 设置为 false
    fun setFalse(key: String) {
        falseKeys.add(key)
    }

    // 重置：只需清空 set（O(1)！）
    fun reset() {
        falseKeys.clear() // 一行搞定，超高效！
    }

    override fun executeOnce(runnable: () -> Unit): Boolean {
        val key = runnable.toString()
        if (isTrue(key)) {
            runnable()
            setFalse(key)
            return true
        }
        return false
    }
}