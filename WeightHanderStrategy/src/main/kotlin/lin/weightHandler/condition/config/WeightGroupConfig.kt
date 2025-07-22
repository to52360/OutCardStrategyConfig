package lin.weightHandler.condition.config

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import lin.myLog
import lin.weightHandler.condition.bean.ConditionGroup
import java.nio.file.Path

class WeightGroupConfig {

     val json = Json {     prettyPrint = true       // 美化输出
        ignoreUnknownKeys = true // 忽略未知字段
        encodeDefaults = true    // 包含默认值
    }
    fun configs():List<ConditionGroup>? {
        val rootPath = System.getProperty("user.dir")
        val weightGroupConfig = Path.of(rootPath,"plugin","config","weightGroup.json")
        val file = weightGroupConfig.toFile()
        if (!file.exists()) {
            myLog.info { "文件不存在" }
            return null
        }

        return json.decodeFromString(ListSerializer(ConditionGroup.serializer()),file.readText())

    }
}