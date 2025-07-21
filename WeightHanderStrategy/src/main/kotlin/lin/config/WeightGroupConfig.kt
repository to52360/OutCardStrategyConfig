package lin.config

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import lin.myLog
import lin.weightHandler.condition.bean.ConditionGroup
import java.nio.file.Path

class WeightGroupConfig {
    val configPath: String = "config/"
    val weightGroupConfig = Path.of(configPath+"weightGroup.json")
    val json = Json {     prettyPrint = true       // 美化输出
        ignoreUnknownKeys = true // 忽略未知字段
        encodeDefaults = true    // 包含默认值
    }
    fun configs(){
        val file = weightGroupConfig.toFile()
        if (!file.exists()) {
            myLog.info { "文件不存在" }
            return
        }

        val result = json.decodeFromString(ListSerializer(ConditionGroup.serializer()),file.readText())
        result.forEach {
            myLog.info {it}
        }
    }
}