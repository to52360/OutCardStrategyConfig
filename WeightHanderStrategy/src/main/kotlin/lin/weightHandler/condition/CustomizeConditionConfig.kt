package lin.weightHandler.condition

interface CustomizeConditionConfig {
    fun id():Int
    fun getCustomizeInfos():Map<Int,Any>
}