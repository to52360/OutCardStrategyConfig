package lin.utils

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @param jarPath todo 暂时这么写
 */
class JarClassLoader(private val jarPath: Path=Path.of(System.getProperty("user.dir"),"plugin"))  {

    fun classLoader(): ClassLoader? {
        val pluginsDir =  jarPath.toFile() // 你的插件目录
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
        val classLoader = URLClassLoader(pathList.toTypedArray(), javaClass.classLoader)
        return classLoader
    }
}