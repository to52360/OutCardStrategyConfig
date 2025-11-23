package lin.config.useDemo

import lin.bean.ChangeGroupId
import lin.config.UseConfig
import lin.config.find.def.BindInfo
import lin.config.find.def.singleBindCardId
import lin.serviceLoader.module.ModulesInfo
import org.koin.core.module.Module
import org.koin.dsl.module

class WarriorBindInfo : ModulesInfo {
    override fun loadModules(): Module {
        return module {
            single<BindInfo> {
                singleBindCardId("ETC_417", UseConfig(ChangeGroupId - 1))
            }
        }
    }
}