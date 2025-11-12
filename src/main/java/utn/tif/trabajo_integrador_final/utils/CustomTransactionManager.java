package utn.tif.trabajo_integrador_final.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Component
public class CustomTransactionManager {

    private final DataSource dataSource;
    private Connection connection;

    @Autowired
    public CustomTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void begin() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        System.out.println(" Transacción iniciada (autoCommit = false)");
    }


    public Connection getConnection() {
        return connection;
    }


    public void commit() {
        if (connection != null) {
            try {
                connection.commit();
                System.out.println(" Transacción confirmada (commit)");
            } catch (SQLException e) {
                System.err.println(" Error en commit: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }
    }


    public void rollback() {
        if (connection != null) {
            try {
                connection.rollback();
                System.out.println(" Transacción revertida (rollback)");
            } catch (SQLException e) {
                System.err.println(" Error en rollback: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }
    }

    public void closeConnection() {
        try {
            connection.close();
            System.out.println(" Conexión cerrada y devuelta al pool");
        } catch (SQLException e) {
            System.err.println(" Error al cerrar conexión: " + e.getMessage());
        } finally {
            connection = null;
        }
    }
}
