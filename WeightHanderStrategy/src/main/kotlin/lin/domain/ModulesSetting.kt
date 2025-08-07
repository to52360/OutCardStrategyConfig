package lin.domain

import lin.lifecycle.LifecycleRegisterImpl
import lin.utils.database.DefDBUrl
import lin.utils.database.SqliteJdbcProvider
import lin.weightHandler.condition.config.ComboInfoDao
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DBModules = module  {
    single { SqliteJdbcProvider(DefDBUrl).jdbcTemplate }
    singleOf(::GroupStrategyDao)
    singleOf(::ComboInfoDao)
}