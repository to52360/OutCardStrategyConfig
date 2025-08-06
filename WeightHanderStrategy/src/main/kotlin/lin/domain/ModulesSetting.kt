package lin.domain

import lin.utils.database.DefDBUrl
import lin.utils.database.SqliteJdbcProvider
import lin.weightHandler.condition.config.GroupStrategyDao
import org.koin.dsl.module

val DBModules = module  {
    single { SqliteJdbcProvider(DefDBUrl).jdbcTemplate }
    single { GroupStrategyDao(get()) }

}