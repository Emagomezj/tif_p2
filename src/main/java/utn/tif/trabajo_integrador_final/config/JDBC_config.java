package utn.tif.trabajo_integrador_final.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBC_config {

    private static final String URL = "jdbc:mysql://localhost:3306/tif_db";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            config.setMaximumPoolSize(10);          // Máximo de conexiones simultáneas
            config.setMinimumIdle(5);               // Conexiones mínimas inactivas
            config.setIdleTimeout(30000);           // Tiempo máx. de inactividad (ms)
            config.setConnectionTimeout(20000);     // Tiempo máx. de espera para obtener conexión (ms)
            config.setMaxLifetime(1800000);         // Vida máxima de una conexión (ms)
            config.setPoolName("TIF-HikariPool");

            dataSource = new HikariDataSource(config);

            System.out.println("✅ Pool de conexiones Hikari inicializado correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar el pool de conexiones: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 Pool de conexiones cerrado correctamente");
        }
    }

    public static void testConnection() {
        System.out.println("🔍 Iniciando prueba de conexión (HikariCP)...");
        try (Connection conn = getConnection()) {
            System.out.println("🎉 ¡Conexión obtenida del pool exitosamente!");
            System.out.println("   Pool activo: " + dataSource.getPoolName());
        } catch (SQLException e) {
            System.err.println("💥 Error al obtener conexión del pool:");
            System.err.println("   " + e.getMessage());
        }
    }
}