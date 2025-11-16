package utn.tif.trabajo_integrador_final.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.SQLException;

@Profile("!test")
public class JDBC_config {
    @Value("${DB_URL}")
    private static String dbUrl;
    @Value("${DB_USER:root}")
    private static String user;

    @Value("${DB_PASS:}")
    private static String pass;

    private static final String URL = dbUrl;
    private static final String USER = user;
    private static final String PASSWORD = pass;

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000);
            config.setMaxLifetime(1800000);
            config.setPoolName("TIF-HikariPool");

            dataSource = new HikariDataSource(config);

            System.out.println("Pool Hikari inicializado correctamente");

        } catch (Exception e) {
            System.err.println("Error al inicializar el pool: " + e);
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool cerrado correctamente");
        }
    }
}
