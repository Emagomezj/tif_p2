package utn.tif.trabajo_integrador_final.DAOS;

import utn.tif.trabajo_integrador_final.models.Prestamo;
import utn.tif.trabajo_integrador_final.utils.CustomTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class PrestamoDAO implements GenericDAO<Prestamo> {

    private final CustomTransactionManager transactionManager;

    @Autowired
    public PrestamoDAO(CustomTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private Prestamo mapRowToPrestamo(ResultSet rs) throws SQLException {
        return new Prestamo.Builder()
                .id(rs.getInt("id"))
                .userId(rs.getString("user_id"))
                .libroId(rs.getString("libro_id"))
                .fechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate())
                .fechaPlazo(rs.getDate("fecha_plazo").toLocalDate())
                .fechaDevolucion(rs.getDate("fecha_devolucion").toLocalDate())
                .estado(rs.getString("estado"))
                .build();
    }

    @Override
    public Prestamo save(Prestamo entity) throws Exception {
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "INSERT INTO prestamos (user_id, libro_id, fecha_prestamo, fecha_plazo, fecha_devolucion, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getUserId());
                ps.setString(2, entity.getLibroId());
                ps.setDate(3, Date.valueOf(entity.getFechaPrestamo()));
                ps.setDate(4, Date.valueOf(entity.getFechaPlazo()));

                if (entity.getFechaDevolucion() != null) {
                    ps.setDate(5, Date.valueOf(entity.getFechaDevolucion()));
                } else {
                    ps.setNull(5, Types.DATE);
                }

                ps.setString(6, entity.getEstado());

                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating loan failed, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating loan failed, no ID obtained.");
                    }
                }
            }

            transactionManager.commit();
            return entity;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new Exception("Error al guardar el préstamo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prestamo> bulkCreate(List<Prestamo> entities) throws Exception {
        List<Prestamo> saved = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "INSERT INTO prestamos (user_id, libro_id, fecha_prestamo, fecha_plazo, fecha_devolucion, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (Prestamo prestamo : entities) {
                    ps.setString(1, prestamo.getUserId());
                    ps.setString(2, prestamo.getLibroId());
                    ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                    ps.setDate(4, Date.valueOf(prestamo.getFechaPlazo()));

                    if (prestamo.getFechaDevolucion() != null) {
                        ps.setDate(5, Date.valueOf(prestamo.getFechaDevolucion()));
                    } else {
                        ps.setNull(5, Types.DATE);
                    }

                    ps.setString(6, prestamo.getEstado());
                    ps.addBatch();
                }

                int[] batchResults = ps.executeBatch();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    int index = 0;
                    while (generatedKeys.next() && index < entities.size()) {
                        entities.get(index).setId(generatedKeys.getInt(1));
                        saved.add(entities.get(index));
                        index++;
                    }
                }
            }

            transactionManager.commit();
            return saved;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new Exception("Error al crear préstamos en lote: " + e.getMessage(), e);
        }
    }

    @Override
    public Prestamo findById(String id) throws Exception {
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "SELECT * FROM prestamos WHERE id = ?";
            Prestamo prestamo = null;

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, Integer.parseInt(id));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        prestamo = mapRowToPrestamo(rs);
                    }
                }
            }

            transactionManager.closeConnection();
            return prestamo;

        } catch (SQLException e) {
            throw new Exception("Error al buscar el préstamo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prestamo> findAll() throws Exception {
        List<Prestamo> list = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "SELECT * FROM prestamos";

            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Prestamo prestamo = mapRowToPrestamo(rs);
                    list.add(prestamo);
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new Exception("Error al buscar todos los préstamos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prestamo> findMany(List<String> ids) throws Exception {
        List<Prestamo> list = new ArrayList<>();
        if (ids.isEmpty()) return list;

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            String query = "SELECT * FROM prestamos WHERE id IN (" + placeholders + ")";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < ids.size(); i++) {
                    ps.setInt(i + 1, Integer.parseInt(ids.get(i)));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prestamo prestamo = mapRowToPrestamo(rs);
                        list.add(prestamo);
                    }
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new Exception("Error al buscar múltiples préstamos: " + e.getMessage(), e);
        }
    }

    @Override
    public Prestamo updateOne(Prestamo entity) throws Exception {
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "UPDATE prestamos SET user_id=?, libro_id=?, fecha_prestamo=?, fecha_plazo=?, " +
                    "fecha_devolucion=?, estado=? WHERE id=?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, entity.getUserId());
                ps.setString(2, entity.getLibroId());
                ps.setDate(3, Date.valueOf(entity.getFechaPrestamo()));
                ps.setDate(4, Date.valueOf(entity.getFechaPlazo()));

                if (entity.getFechaDevolucion() != null) {
                    ps.setDate(5, Date.valueOf(entity.getFechaDevolucion()));
                } else {
                    ps.setNull(5, Types.DATE);
                }

                ps.setString(6, entity.getEstado());
                ps.setInt(7, entity.getId());

                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Updating loan failed, no rows affected.");
                }
            }

            transactionManager.commit();
            return entity;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new Exception("Error al actualizar el préstamo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prestamo> updateMany(List<Prestamo> entities) throws Exception {
        List<Prestamo> updated = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "UPDATE prestamos SET user_id=?, libro_id=?, fecha_prestamo=?, fecha_plazo=?, " +
                    "fecha_devolucion=?, estado=? WHERE id=?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (Prestamo prestamo : entities) {
                    ps.setString(1, prestamo.getUserId());
                    ps.setString(2, prestamo.getLibroId());
                    ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                    ps.setDate(4, Date.valueOf(prestamo.getFechaPlazo()));

                    if (prestamo.getFechaDevolucion() != null) {
                        ps.setDate(5, Date.valueOf(prestamo.getFechaDevolucion()));
                    } else {
                        ps.setNull(5, Types.DATE);
                    }

                    ps.setString(6, prestamo.getEstado());
                    ps.setInt(7, prestamo.getId());
                    ps.addBatch();
                }

                ps.executeBatch();
            }

            transactionManager.commit();
            updated.addAll(entities);
            return updated;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new Exception("Error al actualizar múltiples préstamos: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteOne(String id) throws Exception {
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "DELETE FROM prestamos WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, Integer.parseInt(id));
                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Préstamo no encontrado con ID: " + id);
                }
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new Exception("Error al eliminar el préstamo: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMany(List<String> ids) throws Exception {
        if (ids == null || ids.isEmpty()) return;

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            String query = "DELETE FROM prestamos WHERE id IN (" + placeholders + ")";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < ids.size(); i++) {
                    ps.setInt(i + 1, Integer.parseInt(ids.get(i)));
                }
                ps.executeUpdate();
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new Exception("Error al eliminar múltiples préstamos: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> findByUserId(String userId) throws Exception {
        List<Prestamo> list = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "SELECT * FROM prestamos WHERE user_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prestamo prestamo = mapRowToPrestamo(rs);
                        list.add(prestamo);
                    }
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new Exception("Error al buscar préstamos por usuario: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> findByLibroId(String libroId) throws Exception {
        List<Prestamo> list = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "SELECT * FROM prestamos WHERE libro_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, libroId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prestamo prestamo = mapRowToPrestamo(rs);
                        list.add(prestamo);
                    }
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new Exception("Error al buscar préstamos por libro: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> findByEstado(String estado) throws Exception {
        List<Prestamo> list = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "SELECT * FROM prestamos WHERE estado = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, estado);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prestamo prestamo = mapRowToPrestamo(rs);
                        list.add(prestamo);
                    }
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new Exception("Error al buscar préstamos por estado: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> findPrestamosVencidos() throws Exception {
        List<Prestamo> list = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String query = "SELECT * FROM prestamos WHERE fecha_plazo < CURRENT_DATE AND estado = 'activo'";

            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Prestamo prestamo = mapRowToPrestamo(rs);
                    list.add(prestamo);
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new Exception("Error al buscar préstamos vencidos: " + e.getMessage(), e);
        }
    }
}
