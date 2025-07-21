package json



import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import lin.bean.CardWeightInfo
import lin.weightHandler.condition.bean.ConditionGroup

import java.nio.file.Path

import kotlin.test.Test
@Serializable  // 必须添加这个注解
data class Config(val name: String, val version: Int)

class JsonTest{
    private  val ROOT_PATH = "../"
     val CONFIG_PATH: String = "config/"
    val weightGroupConfig = Path.of(CONFIG_PATH+"weightGroup.json")
    val json = Json {     prettyPrint = true       // 美化输出
        ignoreUnknownKeys = true // 忽略未知字段
        encodeDefaults = true    // 包含默认值
    }
    @Test
    fun test1(){
        val conditionGroups = listOf(ConditionGroup(1, 3.0, 250625013, 2.0))

        println(json.encodeToString(ListSerializer(ConditionGroup.serializer()),conditionGroups))
    }
    @Test
    fun test2(){
       val cardWeightInfo = CardWeightInfo("1",100.0)
        println(json.encodeToString(CardWeightInfo.serializer(),cardWeightInfo))
    }
    @Test
    fun test4(){
        println(Json.encodeToString(Config.serializer(),Config("1",1)))
    }
    @Test
    fun test3(){
        val file = weightGroupConfig.toFile()
        if (!file.exists()) {
            println("文件不存在")
            return
        }

        val result = Json.decodeFromString(ListSerializer(ConditionGroup.serializer()),file.readText())
        result.forEach {
            println(it)
        }

    }
}


