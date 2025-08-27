package lin.serviceLoader.module

import org.koin.core.module.Module

interface ModulesInfo {
    fun getModules(): Module
}