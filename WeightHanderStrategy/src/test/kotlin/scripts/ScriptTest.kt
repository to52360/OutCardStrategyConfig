package scripts
/*
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.*
import kotlin.script.experimental.jvm.*
import kotlin.script.experimental.jvmhost.*
import java.io.File

fun main() {
    val hand = listOf(
        ComboCard("Strike", "Attack", 10),
        ComboCard("Slash", "Attack", 15),
        ComboCard("Defend", "Block", 0)
    )
    val ctx = BattleContext(hand, enemyHp = 40, myHp = 80)

    val scriptFile = File("src/main/resources/rule.kts")
    val compiler = BasicJvmScriptingHost()

    val result = compiler.eval(
        ScriptSource(scriptFile.readText(), scriptFile.name),
        RuleScriptConfig,
        ScriptEvaluationConfiguration {
            constructorArgs(ctx)
        }
    )

    when (result) {
        is ResultWithDiagnostics.Success -> {
            val scriptResult = result.value.returnValue
            println("Result: ${(scriptResult as? ResultValue.Value)?.value}")
        }
        is ResultWithDiagnostics.Failure -> {
            result.reports.forEach { println(it.message) }
        }
    }
}

data class ScriptSource(override val text: String, override val name: String) : SourceCode {
    override val locationId: String
        get() = text

}*/
