package lin.domain

import lin.serviceLoader.parse.DropParse
import lin.serviceLoader.parse.ParseCardRule
import lin.serviceLoader.parse.ParseCardWeightInfo
import lin.serviceLoader.parse.ParseCombo
import lin.utils.database.DefDBUrl
import lin.utils.database.SqliteJdbcProvider
import lin.weightHandler.condition.config.ComboInfoDao
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DBModules = module  {
    single { SqliteJdbcProvider(DefDBUrl).jdbcTemplate }
    singleOf(::GroupStrategyDao)
    singleOf(::ComboInfoDao)
}
val ParseCardWeightInfoModule = module {
    singleOf(::ParseCardRule) bind ParseCardWeightInfo::class
    singleOf(::ParseCombo) bind ParseCardWeightInfo::class
    singleOf(::DropParse) bind ParseCardWeightInfo::class
}