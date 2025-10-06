package group4.hrms.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class để quản lý kết nối database sử dụng HikariCP connection pool
 * 
 * @author Group4
 * @since 1.0
 */
public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    private static HikariDataSource dataSource;
    private static final String DB_PROPERTIES_FILE = "db.properties";

    static {
        try {
            initializeDataSource();
            logger.info("DatabaseUtil đã được khởi tạo thành công");
        } catch (Exception e) {
            logger.error("Không thể khởi tạo DatabaseUtil: {}", e.getMessage(), e);
            // Không throw exception để tránh application crash
            // Sẽ handle trong getConnection() method
        }
    }

    /**
     * Khởi tạo HikariCP DataSource từ file properties
     */
    private static void initializeDataSource() throws IOException {
        Properties props = loadDatabaseProperties();

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(props.getProperty("db.driver"));
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));

        // Cấu hình connection pool
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximum", "20")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimum", "5")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.timeout", "30000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idle", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.lifetime", "1800000")));

        // Test query để kiểm tra connection
        String testQuery = props.getProperty("db.pool.connection.test.query", "SELECT 1");
        config.setConnectionTestQuery(testQuery);

        // Cấu hình bổ sung cho MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Pool name cho monitoring
        config.setPoolName("HRMS-HikariCP");

        dataSource = new HikariDataSource(config);
        logger.info("Khởi tạo HikariCP connection pool thành công với pool size: {}",
                config.getMaximumPoolSize());
    }

    /**
     * Load properties từ file db.properties
     */
    private static Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream(DB_PROPERTIES_FILE)) {

            if (input == null) {
                logger.error("Không tìm thấy file {} trong classpath", DB_PROPERTIES_FILE);
                logger.error("Các file có thể có trong classpath:");
                // List một số files để debug
                throw new IOException("Không tìm thấy file " + DB_PROPERTIES_FILE + " trong classpath");
            }

            props.load(input);
            logger.info("Đã load properties từ file: {}", DB_PROPERTIES_FILE);

            // Validate required properties
            String[] requiredProps = { "db.driver", "db.url", "db.username", "db.password" };
            for (String prop : requiredProps) {
                if (props.getProperty(prop) == null || props.getProperty(prop).trim().isEmpty()) {
                    throw new IOException("Property bắt buộc không có hoặc rỗng: " + prop);
                }
            }

        }
        return props;
    }

    /**
     * Lấy connection từ connection pool
     * 
     * @return Connection object
     * @throws SQLException nếu không thể lấy connection
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            logger.error("DataSource chưa được khởi tạo, thử khởi tạo lại...");
            try {
                initializeDataSource();
            } catch (Exception e) {
                throw new SQLException("Không thể khởi tạo DataSource: " + e.getMessage(), e);
            }
        }

        Connection conn = dataSource.getConnection();
        logger.debug("Lấy connection từ pool thành công");
        return conn;
    }

    /**
     * Đóng connection pool khi shutdown application
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Đã đóng HikariCP connection pool");
        }
    }

    /**
     * Kiểm tra kết nối database
     * 
     * @return true nếu kết nối thành công, false nếu thất bại
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && conn.isValid(5);
            if (isValid) {
                logger.info("Test kết nối database thành công");
            } else {
                logger.warn("Test kết nối database thất bại - connection không hợp lệ");
            }
            return isValid;
        } catch (SQLException e) {
            logger.error("Lỗi khi test kết nối database", e);
            return false;
        }
    }

    /**
     * Lấy thông tin về connection pool hiện tại
     * 
     * @return thông tin pool dưới dạng string
     */
    public static String getPoolInfo() {
        if (dataSource == null) {
            return "DataSource chưa được khởi tạo";
        }

        return String.format("Pool Info - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
    }

    /**
     * Private constructor để ngăn khởi tạo instance
     */
    private DatabaseUtil() {
        throw new UnsupportedOperationException("Utility class không thể khởi tạo instance");
    }
}