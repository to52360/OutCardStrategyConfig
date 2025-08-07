package lin.weightHandler.condition.config

import lin.myLog
import lin.bean.ComboInfo
import lin.weightHandler.condition.context.NotWeight
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class ComboInfoDao(private val jdbcTemplate: JdbcTemplate) {
    fun getAllCombos(): List<ComboInfo> {
       return listOf(ComboInfo(1,1.4,true,arrayOf(1.3,1.1), 10.0))
    }

    /**
     * 查询所有combo_info数据
     */
    fun findAll(): List<ComboInfo> {
        val rowMapper = RowMapper { rs: ResultSet, _: Int ->
            ComboInfo(
                infoId = rs.getInt("info_id"),
                bindId = rs.getDouble("bind_id"),
                isBefore = rs.getBoolean("is_before"),
                depIds = rs.toDouble("dep_ids"),
                comboWeight = rs.getDouble("combo_weight")
            )
        }
        val sql = "SELECT * FROM combo_info"
        return jdbcTemplate.query(sql, rowMapper)
    }
}