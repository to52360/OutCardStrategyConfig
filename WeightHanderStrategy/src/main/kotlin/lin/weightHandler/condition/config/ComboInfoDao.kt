package lin.weightHandler.condition.config

import lin.bean.ComboInfo
import lin.bean.ComboType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class ComboInfoDao(private val jdbcTemplate: JdbcTemplate) {

    /**
     * 查询所有combo_info数据
     */
    fun findAll(): List<ComboInfo> {
        val rowMapper = RowMapper { rs: ResultSet, _: Int ->
            ComboInfo(
                infoId = rs.getInt("info_id"),
                bindId = rs.getDouble("bind_id"),
                comboType = ComboType.fromString(rs.getString("combo_type")),
                depIds = rs.toDouble("dep_ids"),
                comboWeight = rs.getDouble("combo_weight")
            )
        }
        val sql = "SELECT * FROM combo_info"
        return jdbcTemplate.query(sql, rowMapper)
    }
}