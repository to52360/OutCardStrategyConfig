package lin.bean.v1

enum class ComboCardType(val displayName: String, val priority: Int) {
    CORE("核心", 1),
    AUXILIARY("辅助", 2),
    OTHER("其他", 3);
}