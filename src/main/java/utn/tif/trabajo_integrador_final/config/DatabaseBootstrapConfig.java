package utn.tif.trabajo_integrador_final.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Profile("!test")
@Configuration
public class DatabaseBootstrapConfig {

    @Value("${DB_USER:root}")
    private String rawUser;

    @Value("${DB_PASS:}")
    private String rawPassword;
    @Value("${DB_NAME}")
    private String dbName;
    @Value("${DB_RAW_URL}")
    private String dbRaw;
    @Value("${DB_URL}")
    private String dbUrl;


    @PostConstruct
    public void ensureDatabaseExists() {
        System.out.println(dbRaw + "||" + dbUrl);
        createDatabase();
        createTables();
    }

    private void createDatabase() {
        System.out.println(dbRaw);
        try (Connection conn = DriverManager.getConnection(dbRaw, rawUser, rawPassword);
             Statement st = conn.createStatement()) {

            st.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("Base '" + dbName + "' lista");

        } catch (Exception e) {
            throw new RuntimeException("Error creando base de datos", e);
        }
    }

    private void createTables() {
        try (Connection conn = DriverManager.getConnection(dbUrl, rawUser, rawPassword);
             Statement st = conn.createStatement()) {

            String[] tables = {
                    """
            CREATE TABLE IF NOT EXISTS libros (
                id VARCHAR(60) PRIMARY KEY,
                isbn VARCHAR(17) UNIQUE,
                titulo VARCHAR(150) NOT NULL,
                autor VARCHAR(120) NOT NULL,
                editorial VARCHAR(100),
                anio_edicion INT,
                clasificacion_dewey VARCHAR(20),
                estanteria VARCHAR(20),
                idioma VARCHAR(30) DEFAULT 'Español',
                existencias INT DEFAULT 0,
                disponibles INT DEFAULT 0,
                eliminado BOOLEAN DEFAULT FALSE
            );
            """,

                    """
            CREATE TABLE IF NOT EXISTS usuarios (
                id VARCHAR(60) PRIMARY KEY,
                nombre VARCHAR(50) NOT NULL,
                apellido VARCHAR(50) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                fecha_nac DATE NOT NULL,
                hash VARCHAR(200) NOT NULL,
                eliminado BOOLEAN DEFAULT FALSE,
                created_at DATE NOT NULL,
                modified_at DATE NOT NULL,
                pass_last_mod DATE NOT NULL
            );
            """,

                    """
            CREATE TABLE IF NOT EXISTS roles_usuario (
                user_id VARCHAR(60),
                rol VARCHAR(20),
                PRIMARY KEY(user_id, rol),
                FOREIGN KEY (user_id) REFERENCES usuarios(id)
                ON DELETE CASCADE
            );
            """,

                    """
            CREATE TABLE IF NOT EXISTS prestamos (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id VARCHAR(60) NOT NULL,
                libro_id VARCHAR(60) NOT NULL,
                fecha_prestamo DATE NOT NULL,
                fecha_plazo DATE NOT NULL,
                fecha_devolucion DATE NULL,
                estado ENUM('activo', 'devuelto', 'vencido') DEFAULT 'activo',
                FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE ON UPDATE CASCADE
            );
            """
            };

            for (String tableSql : tables) {
                st.execute(tableSql);
            }

            try {
                st.execute("CREATE INDEX idx_prestamos_activos ON prestamos(user_id, estado)");
            } catch (SQLException e) {
                if (e.getErrorCode() == 1061) { // índice duplicado
                    System.out.println("Índice idx_prestamos_activos ya existe, se omite.");
                } else {
                    throw e;
                }
            }

            try {
                st.execute("CREATE INDEX idx_libros_disponibles ON libros(disponibles, eliminado)");
            } catch (SQLException e) {
                if (e.getErrorCode() == 1061) { // índice duplicado
                    System.out.println("Índice idx_libros_disponibles ya existe, se omite.");
                } else {
                    throw e;
                }
            }

            System.out.println("Tablas creadas correctamente");

        } catch (Exception e) {
            throw new RuntimeException("Error creando tablas", e);
        }
    }

}
