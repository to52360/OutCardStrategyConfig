package lin.utils.database

import lin.weightHandler.condition.context.ConditionException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.nio.file.*
import javax.sql.DataSource

/**
 * [club.xiaojiawei.config.DBConfig]
 */
class SqliteJdbcProvider(private val dbPath: Path) {
    /**
     * [org.springframework.jdbc.support.SQLExceptionSubclassTranslator.doTranslate]
     */
    private val dataSource: DataSource by lazy {
        // 1. 检查路径是否合法
        validateDbPath(dbPath)

        // 2. 创建数据源
        DriverManagerDataSource().apply {
            setDriverClassName("org.sqlite.JDBC")
            url = "jdbc:sqlite:${dbPath.toAbsolutePath()}"
        }

    }



    val jdbcTemplate: JdbcTemplate by lazy {
        JdbcTemplate(dataSource)
    }

    private fun validateDbPath(path: Path) {
        // 确保父目录存在
        val parentDir = path.parent
        if (parentDir != null && !Files.exists(parentDir)) {
            throw ConditionException("数据库路径不存在: $parentDir")
        }

        // 如果文件存在，确保是普通文件
        if (Files.exists(path) && !Files.isRegularFile(path)) {
            throw ConditionException("数据库路径不是一个文件: $path")
        }

        // 如果文件不存在，尝试创建（可选）
        if (!Files.exists(path)) {
            throw ConditionException("数据库文件不存在")
        }
    }
}