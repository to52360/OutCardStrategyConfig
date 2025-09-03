package lin.utils.serviceLoader

import lin.myLog
import java.util.*

/**
 * 不知要不要ioc,先object先
 */
object ServiceLoaderUtils {
    val classLoader: ClassLoader by lazy {
        JarClassLoader(parent = javaClass.classLoader).classLoader()?:run {
            myLog.info { "不存在扩展类" }
            javaClass.classLoader
        }
    }
    private val serviceCache: MutableMap<Class<*>, Any> by lazy { mutableMapOf() }

     fun <T> loadServices(serviceType: Class<T>): List<T> {
         return loadServicesByMutable(serviceType)
     }

    fun <T> loadServicesByMutable(serviceType: Class<T>, services: MutableList<T> = mutableListOf()): MutableList<T> {
        loadServicesForEach(serviceType) {
            services.add(it)
            false
        }
        return services
    }

    inline fun <T> loadServicesForEach(serviceType: Class<T>, consumeOrBreak: (T) -> Boolean) {
        val threadClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = classLoader
            val loader = ServiceLoader.load(serviceType)
            for (provider in loader.stream()) {
                try {
                    val service = provider.get()
                    val isBreak = consumeOrBreak(service)
                    if (isBreak) break
                } catch (e: ServiceConfigurationError) {
                    myLog.warn(e) { "跳过服务提供者: ${provider.type().name}，原因: ${e.message}" }
                }
            }
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


