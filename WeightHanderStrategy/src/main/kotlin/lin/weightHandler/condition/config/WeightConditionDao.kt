package lin.weightHandler.condition.config

import lin.serviceLoader.weightRule.WeightCondition
import org.springframework.jdbc.core.JdbcTemplate

/**
 *
 */
class WeightConditionDao(private val jdbcTemplate: JdbcTemplate) {

    /**
     * 将WeightCondition的HashMap写入到weight_condition表中
     * 每次写入前先清空表
     */
    fun saveAll(weightConditions: Collection<WeightCondition>) {
        // 先清空表
        jdbcTemplate.execute("DELETE FROM weight_condition")

        // 批量插入数据
        val sql = "INSERT INTO weight_condition (id, name, description) VALUES (?, ?, ?)"
        jdbcTemplate.batchUpdate(
            sql,
            weightConditions.map { condition ->
                arrayOf(condition.id(), condition.name(), condition.description())
            }
        )
    }

    fun saveIds(groupIds: Set<Double>) {
        // 先清空表
        jdbcTemplate.execute("DELETE FROM weight_group_id")

        // 批量插入数据
        val sql = "INSERT INTO weight_group_id (groupId) VALUES (?)"
        jdbcTemplate.batchUpdate(
            sql,
            groupIds.map { groupId ->
                arrayOf(groupId)
            }
        )
    }

}