package lin.weightHandler.condition.config

import lin.myLog
import lin.utils.database.SqliteJdbcProvider
import lin.weightHandler.condition.bean.ConditionGroup
import lin.weightHandler.condition.context.ConditionException
import java.nio.file.Path

class WeightGroupConfig {
    /**
     * [org.sqlite.jdbc4.JDBC4ResultSet.unsupported]
     */
    fun configs():List<ConditionGroup>? {
        val rootPath = System.getProperty("user.dir")
        val weightGroupConfig = Path.of(rootPath,"weightHandlerStrategy.db")
        try {
            val sqliteJdbcProvider = SqliteJdbcProvider(weightGroupConfig)
            val groupStrategyDao = GroupStrategyDao(sqliteJdbcProvider.jdbcTemplate)
            return groupStrategyDao.getAll()
        }catch (e: ConditionException){
            myLog.warn(e){
                e.message
            }
            return null
        }catch (e:Exception){
            throw e
        }



    }
}