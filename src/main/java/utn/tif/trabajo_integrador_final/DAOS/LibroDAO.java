package utn.tif.trabajo_integrador_final.DAOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import utn.tif.trabajo_integrador_final.entities.Libro;
import utn.tif.trabajo_integrador_final.utils.CustomTransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class LibroDAO implements GenericDAO<Libro> {
    private final CustomTransactionManager transactionManager;

    @Autowired
    public LibroDAO(CustomTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private Libro mapRowToLibro(ResultSet rs) throws SQLException {
        return new Libro.Builder()
                .id(rs.getString("id"))
                .isbn(rs.getString("isbn"))
                .titulo(rs.getString("titulo"))
                .autor(rs.getString("autor"))
                .editorial(rs.getString("editorial"))
                .anioEdicion(rs.getInt("anio_edicion"))
                .clasificacionDewey(rs.getString("clasificacion_dewey"))
                .estanteria(rs.getString("estanteria"))
                .idioma(rs.getString("idioma"))
                .existencias(rs.getInt("existencias"))
                .disponibles(rs.getInt("disponibles"))
                .eliminado(rs.getBoolean("eliminado"))
                .build();
    }

    @Override
    public Libro save(Libro entity)  {
        String sql = "INSERT INTO libros (id, isbn, titulo, autor, editorial, anio_edicion, " +
                "clasificacion_dewey, estanteria, idioma, existencias, disponibles, eliminado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getId());
                ps.setString(2, entity.getIsbn());
                ps.setString(3, entity.getTitulo());
                ps.setString(4, entity.getAutor());
                ps.setString(5, entity.getEditorial());
                ps.setObject(6, entity.getAnioEdicion(), Types.INTEGER);
                ps.setString(7, entity.getClasificacionDewey());
                ps.setString(8, entity.getEstanteria());
                ps.setString(9, entity.getIdioma());
                ps.setObject(10, entity.getExistencias(), Types.INTEGER);
                ps.setObject(11, entity.getDisponibles(), Types.INTEGER);
                ps.setBoolean(12, entity.getEliminado());

                ps.executeUpdate();
            }

            transactionManager.commit();
            return entity;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al guardar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libro> bulkCreate(List<Libro> entities)  {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();

        String sql = "INSERT INTO libros (id, isbn, titulo, autor, editorial, anio_edicion, " +
                "clasificacion_dewey, estanteria, idioma, existencias, disponibles, eliminado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Libro libro : entities) {
                    ps.setString(1, libro.getId());
                    ps.setString(2, libro.getIsbn());
                    ps.setString(3, libro.getTitulo());
                    ps.setString(4, libro.getAutor());
                    ps.setString(5, libro.getEditorial());
                    ps.setObject(6, libro.getAnioEdicion(), Types.INTEGER);
                    ps.setString(7, libro.getClasificacionDewey());
                    ps.setString(8, libro.getEstanteria());
                    ps.setString(9, libro.getIdioma());
                    ps.setObject(10, libro.getExistencias(), Types.INTEGER);
                    ps.setObject(11, libro.getDisponibles(), Types.INTEGER);
                    ps.setBoolean(12, libro.getEliminado());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            transactionManager.commit();
            return entities;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error en bulkCreate libros: " + e.getMessage(), e);
        }
    }

    public Libro iFindById(String id)  {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            Libro libro = null;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) libro = mapRowToLibro(rs);
                }
            }

            transactionManager.closeConnection();
            return libro;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public Libro findById(String id)  {
        String sql = "SELECT * FROM libros WHERE id = ? AND eliminado = FALSE";
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            Libro libro = null;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) libro = mapRowToLibro(rs);
                }
            }

            transactionManager.closeConnection();
            return libro;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libro> findAll()  {
        String sql = "SELECT * FROM libros WHERE eliminado = FALSE";
        List<Libro> list = new ArrayList<>();

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowToLibro(rs));
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los libros: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libro> findMany(List<String> ids)  {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM libros WHERE id IN (" + placeholders + ") AND eliminado = FALSE";
        List<Libro> list = new ArrayList<>();

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < ids.size(); i++) ps.setString(i + 1, ids.get(i));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapRowToLibro(rs));
                }
            }

            transactionManager.closeConnection();
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar múltiples libros: " + e.getMessage(), e);
        }
    }
    @Override
    public Libro updateOne(Libro entity)  {
        String sql = "UPDATE libros SET isbn=?, titulo=?, autor=?, editorial=?, anio_edicion=?, " +
                "clasificacion_dewey=?, estanteria=?, idioma=?, existencias=?, disponibles=?, eliminado=? " +
                "WHERE id=?";

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getIsbn());
                ps.setString(2, entity.getTitulo());
                ps.setString(3, entity.getAutor());
                ps.setString(4, entity.getEditorial());
                ps.setObject(5, entity.getAnioEdicion(), Types.INTEGER);
                ps.setString(6, entity.getClasificacionDewey());
                ps.setString(7, entity.getEstanteria());
                ps.setString(8, entity.getIdioma());
                ps.setObject(9, entity.getExistencias(), Types.INTEGER);
                ps.setObject(10, entity.getDisponibles(), Types.INTEGER);
                ps.setBoolean(11, entity.getEliminado());
                ps.setString(12, entity.getId());
                ps.executeUpdate();
            }

            transactionManager.commit();
            return entity;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al actualizar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libro> updateMany(List<Libro> entities)  {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        try {
            transactionManager.begin();
            for (Libro libro : entities) updateOne(libro);
            transactionManager.commit();
            return entities;

        } catch (Exception e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al actualizar varios libros: " + e.getMessage(), e);
        }
    }
    @Override
    public void deleteOne(String id)  {
        String sql = "UPDATE libros SET eliminado = TRUE WHERE id = ?";
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.executeUpdate();
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al eliminar lógicamente libro: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMany(List<String> ids)  {
        if (ids == null || ids.isEmpty()) return;

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "UPDATE libros SET eliminado = TRUE WHERE id IN (" + placeholders + ")";

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < ids.size(); i++) ps.setString(i + 1, ids.get(i));
                ps.executeUpdate();
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al eliminar varios libros: " + e.getMessage(), e);
        }
    }

    public void totalDeleteOne(String id)  {
        String sql = "DELETE FROM libros WHERE id = ?";
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.executeUpdate();
            }
            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al borrar físicamente libro: " + e.getMessage(), e);
        }
    }

    public void totalDeleteMany(List<String> ids)  {
        if (ids == null || ids.isEmpty()) return;

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "DELETE FROM libros WHERE id IN (" + placeholders + ")";

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < ids.size(); i++) ps.setString(i + 1, ids.get(i));
                ps.executeUpdate();
            }
            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al borrar físicamente varios libros: " + e.getMessage(), e);
        }
    }
}
