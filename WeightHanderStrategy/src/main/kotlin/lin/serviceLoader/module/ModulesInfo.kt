package lin.serviceLoader.module

import org.koin.core.module.Module

interface ModulesInfo {
    fun loadModules(): Module
}