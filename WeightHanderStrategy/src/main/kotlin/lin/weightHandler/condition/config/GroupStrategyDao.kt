package lin.weightHandler.condition.config

import lin.weightHandler.condition.bean.ConditionGroup
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

class GroupStrategyDao(private val jdbcTemplate: JdbcTemplate) {

    fun getAll(): List<ConditionGroup> {
        return jdbcTemplate.query("SELECT * FROM weight_group") { rs, _ ->
            ConditionGroup(
                groupId = rs.getInt("groupId"),
                bindId = rs.getDouble("bindId"),
                weightConditionId  = rs.getInt("weightConditionId"),
                depByWeightId = rs.safeGetDouble("depByWeightId"),
                basePriority = rs.safeGetDouble("basePriority")
            )
        }
    }
    fun ResultSet.safeGetDouble(columnName: String): Double? {
        val value = getObject(columnName)
        return when (value) {
            null -> null
            is Double -> value
            is Float -> value.toDouble()
            is Int -> value.toDouble()
            is Long -> value.toDouble()
            else -> throw IllegalArgumentException("Unsupported type for Double conversion: ${value.javaClass}")
        }
    }

}