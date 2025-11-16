package utn.tif.trabajo_integrador_final.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Profile("!test")
@Component
public class DataLoader {

    @Autowired
    private CustomTransactionManager txManager;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void init() {
        loadUsers();
        loadBooks(5000);
    }

    public void loadUsers() {
        try {
            txManager.begin();
            Connection conn = txManager.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios");
            rs.next();

            if (rs.getInt(1) > 0) {
                System.out.println("Usuarios existentes — no se insertarán nuevos.");
                txManager.commit();
                return;
            }

            System.out.println("Insertando usuarios iniciales...");

            String sql = String.format("""
                INSERT INTO usuarios (id,nombre,apellido,email,fecha_nac,hash,eliminado,created_at,modified_at,pass_last_mod)
                VALUES
                    ('u_admin','Admin','Principal','admin@tif.com','1980-05-01','%s',false,CURDATE(),CURDATE(),CURDATE()),
                    ('u_juan','Juan','Pérez','juan@example.com','1990-01-01','%s',false,CURDATE(),CURDATE(),CURDATE()),
                    ('u_maria','Maria','Gomez','maria@example.com','1995-08-10','%s',false,CURDATE(),CURDATE(),CURDATE()),
                    ('u_biblio','Ana','Librera','bibliotecaria@tif.com','1988-03-12','%s',false,CURDATE(),CURDATE(),CURDATE());
            """,
                    encoder.encode("admin123"),
                    encoder.encode("12345678"),
                    encoder.encode("87654321"),
                    encoder.encode("biblioteca2024")
            );

            st.execute(sql);

            st.execute("""
                INSERT INTO roles_usuario (user_id, rol) VALUES
                    ('u_admin','ADMIN'),
                    ('u_admin','USER'),
                    ('u_juan','USER'),
                    ('u_maria','USER'),
                    ('u_biblio','ADMIN'),
                    ('u_biblio','USER');
            """);

            txManager.commit();
            System.out.println("Usuarios iniciales creados.");

        } catch (Exception e) {
            txManager.rollback();
            throw new RuntimeException("Error insertando usuarios", e);
        }
    }

    public void loadBooks(int cantidad) {
        try {
            txManager.begin();
            Connection conn = txManager.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM libros");
            rs.next();

            if (rs.getInt(1) > 0) {
                System.out.println("Libros existentes — no se insertan nuevos.");
                txManager.commit();
                return;
            }

            System.out.println("Insertando " + cantidad + " libros...");

            String sql = """
                INSERT INTO libros (id,isbn,titulo,autor,editorial,anio_edicion,
                                    clasificacion_dewey,estanteria,idioma,
                                    existencias,disponibles)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = conn.prepareStatement(sql);

            String[] editoriales = {"Penguin","HarperCollins","Planeta","Anagrama","Alfaguara"};
            String[] idiomas = {"Español","English","Français","Deutsch"};
            String[] autores = {"Borges","Cortázar","García Márquez","Piglia","Sábato"};
            String[] clasif = {"100","200","300","500","820"};
            String[] est = {"A1","A2","A3","B1","B2"};

            for (int i = 0; i < cantidad; i++) {
                String id = UUID.randomUUID().toString() + "_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                ps.setString(1, id);
                ps.setString(2, "978-" + (100000 + i));
                ps.setString(3, "Libro generado #" + i);
                ps.setString(4, autores[(int)(Math.random()*autores.length)]);
                ps.setString(5, editoriales[(int)(Math.random()*editoriales.length)]);
                ps.setInt(6, 1950 + (int)(Math.random()*70));
                ps.setString(7, clasif[(int)(Math.random()*clasif.length)]);
                ps.setString(8, est[(int)(Math.random()*est.length)]);
                ps.setString(9, idiomas[(int)(Math.random()*idiomas.length)]);

                int ex = 1 + (int)(Math.random()*10);
                ps.setInt(10, ex);
                ps.setInt(11, Math.max(0, ex - (int)(Math.random()*3)));

                ps.addBatch();

                if (i % 1000 == 0) ps.executeBatch();
            }

            ps.executeBatch();
            txManager.commit();
            System.out.println("Libros insertados: " + cantidad);

        } catch (Exception e) {
            txManager.rollback();
            throw new RuntimeException("Error insertando libros", e);
        }
    }
}
