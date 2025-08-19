package lin.utils.serviceLoader

import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path

/**
 * @param jarPath todo 暂时这么写
 */
class JarClassLoader(
    private val jarPath: Path = Path.of(System.getProperty("user.dir"), "plugin", "weightHandlerStrategy"),
    private val parent: ClassLoader = Thread.currentThread().contextClassLoader,

    ) {

    fun classLoader(): ClassLoader? {
        val pluginsDir =  jarPath.toFile() // 你的插件目录
        if(!pluginsDir.exists()){return null} //不存在扩展

        val jars = pluginsDir.listFiles{
            _,name
            -> name.endsWith(".jar")
        }
        val pathList = mutableListOf<URL>()
        jars?.forEach {
            pathList.add(it.toURI().toURL())
        }?:run{
            return null
        }
        val classLoader = URLClassLoader(pathList.toTypedArray(), this.parent)
        return classLoader
    }
}