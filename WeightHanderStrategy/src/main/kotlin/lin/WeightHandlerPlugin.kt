package lin


import club.xiaojiawei.hsscriptstrategysdk.StrategyPlugin
import io.github.oshai.kotlinlogging.KotlinLogging

val myLog = KotlinLogging.logger {}
class WeightHandlerPlugin: StrategyPlugin {
    override fun version(): String = "1.0.0"

    override fun author(): String = "lin"

    override fun description(): String =
        """
        基于权重处理程序计算权重,目前有基于战场条件计算权重
        """.trimIndent()

    override fun id(): String = this.javaClass.name

    override fun name(): String = "权重处理策略"

    override fun homeUrl(): String = "https://github.com/xjw580/Hearthstone-Script"
    override fun cardSDKVersion() = "4.9.0-GA"

    override fun strategySDKVersion() = "4.9.0-GA"


}