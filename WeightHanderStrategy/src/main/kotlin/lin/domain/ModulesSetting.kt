package lin.domain

import lin.domain.combo.*
import lin.serviceLoader.parse.ParseCardRule
import lin.serviceLoader.parse.ParseCardWeightInfo
import lin.serviceLoader.parse.ParseCombo
import lin.utils.database.DefDBUrl
import lin.utils.database.SqliteJdbcProvider
import lin.utils.database.dao.CardInfoDao
import lin.weightHandler.condition.config.ComboInfoDao
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DBModules = module  {
    single { SqliteJdbcProvider(DefDBUrl).jdbcTemplate }
    singleOf(::GroupStrategyDao)
    singleOf(::ComboInfoDao)
    singleOf(::CardInfoDao)
}
val ParseCardWeightInfoModule = module {
    singleOf(::ParseCardRule) bind ParseCardWeightInfo::class
    singleOf(::ParseCombo) bind ParseCardWeightInfo::class
}

val ComBoInfoModule = module {
    singleOf(::LastUseCombo) { named(LAST) } bind ComboParse::class
    singleOf(::ComboImpl) { named(DEF) } bind ComboParse::class
    singleOf(::ComboImpl) { named(BEFORE) } bind ComboParse::class
    singleOf(::ChangeComboParse) { named(CHANGE) } bind ComboParse::class
}