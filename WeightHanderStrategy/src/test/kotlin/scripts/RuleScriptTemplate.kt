package scripts

/*
import kotlin.script.experimental.annotations.*
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.*

data class ComboCard(val name: String, val type: String, val damage: Int)
data class BattleContext(val hand: List<ComboCard>, val enemyHp: Int, val myHp: Int)

@KotlinScript(
    fileExtension = "kts",
    compilationConfiguration = RuleScriptConfig::class
)
abstract class RuleScriptTemplate(val ctx: BattleContext)

object RuleScriptConfig : ScriptCompilationConfiguration({
    baseClass(RuleScriptTemplate::class)
    jvm {
        dependenciesFromClassContext(
            RuleScriptTemplate::class,
            wholeClasspath = true
        )
    }
}) {
    private fun readResolve(): Any = RuleScriptConfig
}*/
