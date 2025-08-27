package drop

import drop.parse.DropParse
import lin.serviceLoader.module.ModulesInfo
import lin.serviceLoader.parse.ParseCardWeightInfo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ModuleInfo : ModulesInfo {
    override fun getModules(): Module {
        val parseCardWeightInfoModule = module {
            singleOf(::DropParse) bind ParseCardWeightInfo::class
        }
        return parseCardWeightInfoModule
    }

}
