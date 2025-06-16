package lin.strategy

enum class ComboRole(name:String ) {
    DEP("依赖项"),     // 依赖项,打出参考条件
    SUPPORT("辅助"),       // 辅助:检索组件/减费 提升combo成功率
    ROOT("根节点"),         // 主组组件
    OTHER("其他"),       //未定义
}