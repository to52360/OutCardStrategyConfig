package lin.utils.serviceLoader

import lin.myLog
import java.util.*

/**
 * 不知要不要ioc,先object先
 */
object ServiceLoaderUtils {
    private val classLoader : ClassLoader by lazy {
        JarClassLoader(parent = javaClass.classLoader).classLoader()?:run {
            myLog.info { "不存在扩展类" }
            javaClass.classLoader
        }
    }
    private val serviceCache: MutableMap<Class<*>, Any> by lazy { mutableMapOf() }

     fun <T> loadServices(serviceType: Class<T>): List<T> {
        val threadClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = classLoader
            val loader = ServiceLoader.load(serviceType)
            val services = mutableListOf<T>()
            for (provider in loader.stream()) {
                try {
                    val service = provider.get()
                    services.add(service)
                } catch (e: ServiceConfigurationError) {
                    myLog.warn(e) { "跳过服务提供者: ${provider.type().name}，原因: ${e.message}" }
                }
            }
            return services.toList()
        }finally {
            Thread.currentThread().contextClassLoader = threadClassLoader
        }

    }
    @Suppress("UNCHECKED_CAST")
    fun <T> getCacheServices(serviceType: Class<T>): List<T> {
        return serviceCache.getOrPut(serviceType) {
            loadServices(serviceType)
        } as List<T>
    }

    fun <T> getCacheFirstOrNull(serviceType: Class<T>): T? {
        return getCacheServices(serviceType).firstOrNull()
    }
}


