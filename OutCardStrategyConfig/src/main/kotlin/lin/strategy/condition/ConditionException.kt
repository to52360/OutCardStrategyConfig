package lin.strategy.condition

class ConditionException(any:Any,mes: String ?="条件组出现异常"):RuntimeException(any.javaClass.simpleName+":"+mes)