package lin.domain

import club.xiaojiawei.hsscriptcardsdk.status.WAR
import lin.domain.combo.*
import lin.domain.combo.ComboParse.Companion.BEFORE
import lin.domain.combo.ComboParse.Companion.CHANGE
import lin.domain.combo.ComboParse.Companion.DEF
import lin.domain.combo.ComboParse.Companion.FIRST
import lin.domain.combo.ComboParse.Companion.LAST
import lin.domain.strategy.*
import lin.lifecycle.LifecycleRegister
import lin.lifecycle.LifecycleRegisterImpl
import lin.serviceLoader.findCombo.SkillFindStrategy
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
import lin.weightHandler.condition.config.WeightConditionDao
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

        //ui广告
        singleOf(::WeightConditionDao)
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
        singleOf(::FirstUseCombo) { named(FIRST) } bind ComboParse::class
    }

    val mainModule = module {
        single { WAR }
        singleOf(::MyWarManage)
        singleOf(::WeightHandlerDomain)
    }

    val utilsModule = module {
        singleOf(::UseStrategyUtils)
        singleOf(::FindPlanner)
    }
    val findStrategy = module {
        singleOf(::ExtCostStrategy) bind FindComboStrategy::class
        //singleOf(::ForgeFindStrategy) bind FindComboStrategy::class
        singleOf(::DefFindStrategy) bind FindComboStrategy::class
        singleOf(::SkillFindStrategy) bind FindComboStrategy::class

    }

    fun loadModules() {
        startKoin {
            modules(mainModule, dbModules, parseCardWeightInfoModule, comBoInfoModule, utilsModule, findStrategy)
            modules(module { singleOf(::LifecycleRegisterImpl) bind LifecycleRegister::class })
            val extraModule = ServiceLoaderUtils.loadServices(ModulesInfo::class.java)
            extraModule.forEach {
                it.loadModules()
            }
        }

    }


}