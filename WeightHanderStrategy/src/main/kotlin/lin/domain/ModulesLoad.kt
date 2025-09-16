package lin.domain

import club.xiaojiawei.hsscriptcardsdk.status.WAR
import lin.WeightHandlerStrategy
import lin.domain.combo.BEFORE
import lin.domain.combo.CHANGE
import lin.domain.combo.ChangeComboParse
import lin.domain.combo.ComboImpl
import lin.domain.combo.ComboParse
import lin.domain.combo.DEF
import lin.domain.combo.LAST
import lin.domain.combo.LastUseCombo
import lin.lifecycle.LifecycleRegister
import lin.lifecycle.LifecycleRegisterImpl
import lin.serviceLoader.module.ModulesInfo
import lin.serviceLoader.parse.LieRenParse
import lin.serviceLoader.parse.ParseCardRule
import lin.serviceLoader.parse.ParseCardWeightInfo
import lin.serviceLoader.parse.ParseCombo
import lin.utils.database.DefDBUrl
import lin.utils.database.SqliteJdbcProvider
import lin.utils.database.dao.CardInfoDao
import lin.utils.serviceLoader.ServiceLoaderUtils
import lin.weightHandler.condition.config.ComboInfoDao
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ModulesLoad {
    val dbModules = module {
        single { SqliteJdbcProvider(DefDBUrl).jdbcTemplate }
        singleOf(::GroupStrategyDao)
        singleOf(::ComboInfoDao)
        singleOf(::CardInfoDao)
    }
    val parseCardWeightInfoModule = module {
        singleOf(::ParseCardRule) bind ParseCardWeightInfo::class
        singleOf(::ParseCombo) bind ParseCardWeightInfo::class
        singleOf(::LieRenParse) bind ParseCardWeightInfo::class
    }

    val comBoInfoModule = module {
        singleOf(::LastUseCombo) { named(LAST) } bind ComboParse::class
        singleOf(::ComboImpl) { named(DEF) } bind ComboParse::class
        singleOf(::ComboImpl) { named(BEFORE) } bind ComboParse::class
        singleOf(::ChangeComboParse) { named(CHANGE) } bind ComboParse::class
    }

    val mainModule = module {
        single { WAR }
        singleOf(::MyWarManage)
        singleOf(::WeightHandlerDomain)
    }

    fun loadModules() {
        startKoin {
            modules(mainModule, dbModules, parseCardWeightInfoModule, comBoInfoModule)
            modules(module { singleOf(::LifecycleRegisterImpl) bind LifecycleRegister::class })
            val extraModule = ServiceLoaderUtils.loadServices(ModulesInfo::class.java)
            extraModule.forEach {
                it.loadModules()
            }
        }

    }
}