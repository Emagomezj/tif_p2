package utn.tif.trabajo_integrador_final.DAOS;
import utn.tif.trabajo_integrador_final.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import utn.tif.trabajo_integrador_final.utils.CustomTransactionManager;
import utn.tif.trabajo_integrador_final.constants.Roles;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDAO implements GenericDAO<Usuario> {
    private final CustomTransactionManager transactionManager;

    @Autowired
    public UserDAO(CustomTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    private List<String> getRolesWithDefault(Usuario usuario) {
        List<String> roles = usuario.getRoles();

        if (roles == null || roles.isEmpty()) {
            return List.of(Roles.USER.name());
        }
        return roles;
    }

    private Usuario mapRowToUsuario(ResultSet rs) throws SQLException {
        return new Usuario.Builder()
                .id(rs.getString("id"))
                .nombre(rs.getString("nombre"))
                .apellido(rs.getString("apellido"))
                .email(rs.getString("email"))
                .fechaNac(rs.getDate("fecha_nac").toLocalDate())
                .hash(rs.getString("hash"))
                .eliminado(rs.getBoolean("eliminado"))
                .createdAt(rs.getDate("created_at").toLocalDate())
                .modifiedAt(rs.getDate("modified_at").toLocalDate())
                .passLastMod(rs.getDate("pass_last_mod").toLocalDate())
                .build();
    }

    @Override
    public Usuario save(Usuario entity)  {
        try{
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String query = "INSERT INTO usuarios " +
                    "(id, nombre, apellido, email, fecha_nac, hash, eliminado, created_at, modified_at, pass_last_mod) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, entity.getId());
                ps.setString(2, entity.getNombre());
                ps.setString(3, entity.getApellido());
                ps.setString(4, entity.getEmail());
                if (entity.getFechaNac() != null) {
                    ps.setDate(5, java.sql.Date.valueOf(entity.getFechaNac()));
                } else {
                    ps.setNull(5, Types.DATE);
                }
                ps.setString(6, entity.getHash());
                ps.setBoolean(7, entity.getEliminado());
                if (entity.getCreatedAt() != null) {
                    ps.setDate(8, java.sql.Date.valueOf(entity.getCreatedAt()));
                } else {
                    ps.setNull(8, Types.DATE);
                }

                if (entity.getModifiedAt() != null) {
                    ps.setDate(9, java.sql.Date.valueOf(entity.getModifiedAt()));
                } else {
                    ps.setNull(9, Types.DATE);
                }

                if (entity.getPassLastMod() != null) {
                    ps.setDate(10, java.sql.Date.valueOf(entity.getPassLastMod()));
                } else {
                    ps.setNull(10, Types.DATE);
                }
                ps.executeUpdate();
            }
            String sqlRole = "INSERT INTO roles_usuario (user_id, rol) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlRole)) {
                for (String rol : getRolesWithDefault(entity)) {
                    ps.setString(1, entity.getId());
                    ps.setString(2, rol);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            transactionManager.commit();
            return entity;
        }catch (SQLException e){
            transactionManager.rollback();
            //e.printStackTrace();
            throw new RuntimeException("Error al guardar el usuario: "+ e.getMessage(), e);
        }

    }
    @Override
    public List<Usuario> bulkCreate(List<Usuario> entities) {
        List<Usuario> saved = new ArrayList<>();
        try{
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String sqlUser = "INSERT INTO usuarios " +
                    "(id, nombre, apellido, email, fecha_nac, hash, eliminado, created_at, modified_at, pass_last_mod) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlRole = "INSERT INTO roles_usuario (user_id, rol) VALUES (?, ?)";
            try (PreparedStatement psUser = conn.prepareStatement(sqlUser);
                 PreparedStatement psRole = conn.prepareStatement(sqlRole)) {

                for (Usuario u : entities) {
                    psUser.setString(1, u.getId());
                    psUser.setString(2, u.getNombre());
                    psUser.setString(3, u.getApellido());
                    psUser.setString(4, u.getEmail());
                    if (u.getFechaNac() != null) {
                        psUser.setDate(5, java.sql.Date.valueOf(u.getFechaNac()));
                    } else {
                        psUser.setNull(5, Types.DATE);
                    }
                    psUser.setString(6, u.getHash());
                    psUser.setBoolean(7, u.getEliminado());
                    if (u.getCreatedAt() != null) {
                        psUser.setDate(8, java.sql.Date.valueOf(u.getCreatedAt()));
                    } else {
                        psUser.setNull(8, Types.DATE);
                    }

                    if (u.getModifiedAt() != null) {
                        psUser.setDate(9, java.sql.Date.valueOf(u.getModifiedAt()));
                    } else {
                        psUser.setNull(9, Types.DATE);
                    }

                    if (u.getPassLastMod() != null) {
                        psUser.setDate(10, java.sql.Date.valueOf(u.getPassLastMod()));
                    } else {
                        psUser.setNull(10, Types.DATE);
                    }
                    psUser.addBatch();

                    for (String rol : getRolesWithDefault(u)) {
                        psRole.setString(1, u.getId());
                        psRole.setString(2, rol);
                        psRole.addBatch();
                    }
                }

                psUser.executeBatch();
                psRole.executeBatch();
            }

            transactionManager.commit();
            saved.addAll(entities);
            return saved;

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al bulkCreate usuarios: " + e.getMessage(), e);
        }
    }

    public Usuario iFindById(String id) {
        try{
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String query = "SELECT * FROM usuarios WHERE id = ?";
            Usuario u = null;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()){
                        u = mapRowToUsuario(rs);
                    }
                }
                if (u != null) {
                    String sqlRoles = "SELECT rol FROM roles_usuario WHERE user_id = ?";
                    try (PreparedStatement ps1 = conn.prepareStatement(sqlRoles)) {
                        ps1.setString(1, id);
                        try (ResultSet rs = ps1.executeQuery()) {
                            List<String> roles = new ArrayList<>();
                            while (rs.next()) roles.add(rs.getString("rol"));
                            u.setRoles(roles);
                        }
                    }
                }
            }
            transactionManager.closeConnection();
            return u;
        } catch (SQLException e){
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario findById(String id)  {
        try{
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String query = "SELECT * FROM usuarios WHERE id = ? AND eliminado = FALSE";
            Usuario u = null;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                 if(rs.next()){
                    u = mapRowToUsuario(rs);
                 }
                }
                if (u != null) {
                    String sqlRoles = "SELECT rol FROM roles_usuario WHERE user_id = ?";
                    try (PreparedStatement ps1 = conn.prepareStatement(sqlRoles)) {
                        ps1.setString(1, id);
                        try (ResultSet rs = ps1.executeQuery()) {
                            List<String> roles = new ArrayList<>();
                            while (rs.next()) roles.add(rs.getString("rol"));
                            u.setRoles(roles);
                        }
                    }
                }
            }
            transactionManager.closeConnection();
            return u;
        } catch (SQLException e){
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Usuario> findAll()  {
        List<Usuario> list = new ArrayList<>();
        try{
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String query = "SELECT * FROM usuarios where eliminado = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Usuario u = mapRowToUsuario(rs);
                    String sqlRoles = "SELECT rol FROM roles_usuario WHERE user_id = ?";
                    try(PreparedStatement psRole = conn.prepareStatement(sqlRoles)) {
                        psRole.setString(1, u.getId());
                        List<String> roles = new ArrayList<>();
                        try (ResultSet rsRole = psRole.executeQuery()) {
                            while (rsRole.next()) roles.add(rsRole.getString("rol"));
                            u.setRoles(roles);
                        }
                    }
                    list.add(u);
                }
            }
            transactionManager.closeConnection();
            return list;
        } catch (SQLException e){
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Usuario> findMany(List<String> ids)  {
        List<Usuario> list = new ArrayList<>();
        if(ids.isEmpty()) return list;
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String placeholders = String.join(",", ids.stream().map(id -> "?").toArray(String[]::new));
            String sqlUser = "SELECT * FROM usuarios WHERE id IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                for (int i = 0; i < ids.size(); i++) ps.setString(i + 1, ids.get(i));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Usuario u = mapRowToUsuario(rs);

                        String sqlRoles = "SELECT rol FROM roles_usuario WHERE user_id = ?";
                        try (PreparedStatement psRole = conn.prepareStatement(sqlRoles)) {
                            psRole.setString(1, u.getId());
                            try (ResultSet rsRole = psRole.executeQuery()) {
                                List<String> roles = new ArrayList<>();
                                while (rsRole.next()) roles.add(rsRole.getString("rol"));
                                u.setRoles(roles);
                            }
                        }

                        list.add(u);
                    }
                }
            }
            transactionManager.closeConnection();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuarios: " + e.getMessage(), e);
        }
    }
    public Usuario findByEmail(String email)  {
        try{
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();
            String query = "SELECT * FROM usuarios WHERE email = ? AND eliminado = FALSE";
            Usuario u = null;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        u = mapRowToUsuario(rs);
                    }
                }
                if (u != null) {
                    String sqlRoles = "SELECT rol FROM roles_usuario WHERE user_id = ?";
                    try (PreparedStatement psRole = conn.prepareStatement(sqlRoles)) {
                        psRole.setString(1, u.getId());
                        try (ResultSet rsRole = psRole.executeQuery()) {
                            List<String> roles = new ArrayList<>();
                            while (rsRole.next()) roles.add(rsRole.getString("rol"));
                            u.setRoles(roles);
                        }
                    }
                }
            }catch (SQLException e){
                throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
            }
            transactionManager.closeConnection();
            return u;
        }catch (SQLException e){
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }
    @Override
    public Usuario updateOne(Usuario entity)  {
        try {
            System.out.println("Entra a updateOne");
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String sqlUser = "UPDATE usuarios SET nombre=?, apellido=?, email=?, hash=?, eliminado=?, modified_at=?, pass_last_mod=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, entity.getNombre());
                ps.setString(2, entity.getApellido());
                ps.setString(3, entity.getEmail());
                ps.setString(4, entity.getHash());
                ps.setBoolean(5, entity.getEliminado());
                if (entity.getModifiedAt() != null) {
                    ps.setDate(6, java.sql.Date.valueOf(entity.getModifiedAt()));
                } else {
                    ps.setNull(6, Types.DATE);
                }
                if (entity.getPassLastMod() != null) {
                    ps.setDate(7, java.sql.Date.valueOf(entity.getPassLastMod()));
                } else {
                    ps.setNull(7, Types.DATE);
                }
                ps.setString(8, entity.getId());
                ps.executeUpdate();
            }


            String sqlDeleteRoles = "DELETE FROM roles_usuario WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteRoles)) {
                ps.setString(1, entity.getId());
                ps.executeUpdate();
            }

            String sqlInsertRoles = "INSERT INTO roles_usuario (user_id, rol) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertRoles)) {
                for (String rol : entity.getRoles()) {
                    ps.setString(1, entity.getId());
                    ps.setString(2, rol);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            transactionManager.commit();
            return entity;

        } catch (SQLException e) {
            transactionManager.rollback();
            //e.printStackTrace();
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Usuario> updateMany(List<Usuario> entities)  {
        List<Usuario> updated = new ArrayList<>();
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            for (Usuario u : entities) {
                updateOne(u);
                updated.add(u);
            }

            transactionManager.commit();
            return updated;

        } catch (Exception e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al actualizar muchos usuarios: " + e.getMessage(), e);
        }
    }

    public void totalDeleteOne(String id)  {
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM roles_usuario WHERE user_id = ?")) {
                ps.setString(1, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
                ps.setString(1, id);
                ps.executeUpdate();
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al borrar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteOne(String id)  {
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE usuarios SET eliminado = true WHERE id = ?")) {
                ps.setString(1, id);
                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Usuario no encontrado con ID: " + id);
                }
            }


            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al eliminar lógicamente usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMany(List<String> ids)  {
        if (ids == null || ids.isEmpty()) return;

        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            String sqlUsuarios = "UPDATE usuarios SET eliminado = true WHERE id IN (" + placeholders + ")";

            try (PreparedStatement ps = conn.prepareStatement(sqlUsuarios)) {
                for (int i = 0; i < ids.size(); i++) {
                    ps.setString(i + 1, ids.get(i));
                }
                ps.executeUpdate();
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al eliminar lógicamente usuarios: " + e.getMessage(), e);
        }
    }
    public void totalDeleteMany(List<String> ids)  {
        if (ids == null || ids.isEmpty()) return;
        try {
            transactionManager.begin();
            Connection conn = transactionManager.getConnection();

            String placeholders = String.join(",", ids.stream().map(i -> "?").toArray(String[]::new));
            String sqlRoles = "DELETE FROM roles_usuario WHERE user_id IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoles)) {
                for (int i = 0; i < ids.size(); i++) ps.setString(i + 1, ids.get(i));
                ps.executeUpdate();
            }

            String sqlUsuarios = "DELETE FROM usuarios WHERE id IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuarios)) {
                for (int i = 0; i < ids.size(); i++) ps.setString(i + 1, ids.get(i));
                ps.executeUpdate();
            }

            transactionManager.commit();

        } catch (SQLException e) {
            transactionManager.rollback();
            throw new RuntimeException("Error al borrar muchos usuarios: " + e.getMessage(), e);
        }
    }
}
