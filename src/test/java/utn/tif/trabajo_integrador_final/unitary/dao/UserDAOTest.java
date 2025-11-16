package utn.tif.trabajo_integrador_final.unitary.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utn.tif.trabajo_integrador_final.DAOS.UserDAO;
import utn.tif.trabajo_integrador_final.constants.Roles;
import utn.tif.trabajo_integrador_final.entities.Usuario;
import utn.tif.trabajo_integrador_final.utils.CustomTransactionManager;
import utn.tif.trabajo_integrador_final.utils.Hasher;
import utn.tif.trabajo_integrador_final.utils.Id_generator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
class UserDAOTest {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Hasher hasher;
    @Autowired
    private DataSource dataSource;
    private Usuario usuario;
    private Usuario usuario2;
    private Usuario usuario3;



    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(Id_generator.generarId());
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juanperez@example.com");
        usuario.setHash(this.hasher.hash("contraseña123"));
        usuario.setFechaNac(LocalDate.of(1990, 1, 1));
        ArrayList<String> roles = new ArrayList<>();
        roles.add(Roles.USER.name());
        roles.add(Roles.ADMIN.name());
        usuario.setRoles(roles);
        usuario.setModifiedAt(LocalDate.now());
        usuario.setCreatedAt(LocalDate.now());
        usuario.setPassLastMod(LocalDate.now());

        usuario2 = new Usuario();
        usuario2.setId(Id_generator.generarId());
        usuario2.setNombre("María");
        usuario2.setApellido("Gómez");
        usuario2.setFechaNac(LocalDate.of(1990, 1, 1));
        usuario2.setEmail("mariagomez@example.com");
        usuario2.setHash(this.hasher.hash("5678"));
        usuario2.setModifiedAt(LocalDate.now());
        usuario2.setCreatedAt(LocalDate.now());
        usuario2.setPassLastMod(LocalDate.now());

        usuario3 = new Usuario();
        usuario3.setId(Id_generator.generarId());
        usuario3.setNombre("Carlos");
        usuario3.setApellido("López");
        usuario3.setEmail("carloslopez@example.com");
        usuario3.setFechaNac(LocalDate.of(1990, 1, 1));
        usuario3.setHash(this.hasher.hash("9012"));
        usuario3.setModifiedAt(LocalDate.now());
        usuario3.setCreatedAt(LocalDate.now());
        usuario3.setPassLastMod(LocalDate.now());
    }

    @BeforeEach
    void limpiar() throws Exception {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {

            st.execute("DELETE FROM roles_usuario");
            st.execute("DELETE FROM usuarios");
        }
    }

    @Test
    void testGuardarUsuario() {
        Usuario saved = userDAO.save(usuario);
        assertNotNull(saved.getId());
        assertEquals("Juan", saved.getNombre());
        assertEquals(usuario.getRoles(), saved.getRoles());
        assertEquals(saved.getHash(), usuario.getHash());
        assertEquals(LocalDate.of(1990, 1, 1), usuario.getFechaNac());
        assertEquals(usuario.getId(), saved.getId());
        assertThrows(RuntimeException.class, () -> userDAO.save(usuario));
        usuario.setId(Id_generator.generarId());
        assertThrows(RuntimeException.class, () -> userDAO.save(usuario));
    }
    
    @Test
    void testCreacionMultipleUsuario(){
        Usuario saved = userDAO.save(usuario);
        Usuario saved2 = userDAO.save(usuario2);
        Usuario saved3 = userDAO.save(usuario3);
        assertNotNull(saved);
        assertNotNull(saved2);
        assertNotNull(saved3);
        Usuario found = userDAO.findById(saved.getId());
        Usuario found2 = userDAO.findById(saved2.getId());
        Usuario found3 = userDAO.findById(saved3.getId());
        assertEquals(saved.getId(), found.getId());
        assertEquals(saved2.getNombre(), found2.getNombre());
        assertEquals(saved3.getApellido(), found3.getApellido());
    }


    @Test
    void testBuscarPorId(){
        Usuario saved = userDAO.save(usuario);
        Usuario found = userDAO.findById(saved.getId());

        assertNotNull(found);
        assertEquals("Pérez", found.getApellido());
    }

    @Test
    void buscarVariosUsuario(){
        Usuario saved = userDAO.save(usuario);
        Usuario saved2 = userDAO.save(usuario2);
        Usuario saved3 = userDAO.save(usuario3);
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(
                usuario.getId(),
                usuario2.getId(),
                usuario3.getId()
        ));
        List<Usuario> found = userDAO.findMany(ids);
        assertNotNull(found);
        assertEquals(3, found.size());
    }

    @Test
    void testBuscarPorEmail(){
        Usuario saved = userDAO.save(usuario);
        Usuario found = userDAO.findByEmail(usuario.getEmail());
        assertNotNull(found);
        assertEquals(usuario.getEmail(), found.getEmail());
        assertEquals(usuario.getId(), found.getId());
    }

    @Test
    void testActualizarUsuario() {
        Usuario saved = userDAO.save(usuario);
        saved.setNombre("Carlos");
        userDAO.updateOne(saved);

        Usuario actualizado = userDAO.findById(saved.getId());
        assertNotNull(actualizado);
        assertEquals("Carlos", actualizado.getNombre());
    }

    @Test
    void actualizarVariosUsuario(){
        Usuario saved = userDAO.save(usuario);
        Usuario saved2 = userDAO.save(usuario2);
        Usuario saved3 = userDAO.save(usuario3);
        usuario.setHash(this.hasher.hash("a1b2c3d4"));
        usuario2.setEmail("prueba@prueba.com");
        usuario3.setEmail("prueba@prueba.com");
        ArrayList<Usuario>  usuarios = new ArrayList<>(Arrays.asList(
                usuario, usuario2
        ));
        List<Usuario> updated = userDAO.updateMany(usuarios);
        assertThrows(RuntimeException.class, () -> userDAO.updateOne(usuario3));
        assertEquals(usuario.getHash(), updated.get(0).getHash());
        assertEquals(usuario2.getEmail(), updated.get(1).getEmail());
    }

    @Test
    void testEliminarUsuario() {
        Usuario saved = userDAO.save(usuario);
        userDAO.deleteOne(saved.getId());

        Usuario eliminated = userDAO.findById(saved.getId());

        assertNull(eliminated);
    }

    @Test
    void testEliminarVariosUsuario(){
        Usuario saved = userDAO.save(usuario);
        Usuario saved2 = userDAO.save(usuario2);
        Usuario saved3 = userDAO.save(usuario3);
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(
                saved.getId(),saved2.getId(),saved3.getId())
        );
        userDAO.deleteMany(ids);
        assertNotNull(userDAO.iFindById(saved.getId()));
        assertNotNull(userDAO.iFindById(saved2.getId()));
        assertNotNull(userDAO.iFindById(saved3.getId()));
        assertNull(userDAO.findById(saved.getId()));
        assertNull(userDAO.findById(saved2.getId()));
        assertNull(userDAO.findById(saved3.getId()));
    }

    @Test
    void totalEliminacionIndividualUsuario(){
        Usuario saved = userDAO.save(usuario);
        userDAO.totalDeleteOne(saved.getId());
        assertNull(userDAO.findById(saved.getId()));
        assertNull(userDAO.iFindById(saved.getId()));
    }

    @Test
    void totalEliminacionVariosUsuario(){
        Usuario saved = userDAO.save(usuario);
        Usuario saved2 = userDAO.save(usuario2);
        Usuario saved3 = userDAO.save(usuario3);
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(
                saved.getId(),saved2.getId(),saved3.getId()
        ));
        userDAO.totalDeleteMany(ids);
        assertNull(userDAO.findById(saved.getId()));
        assertNull(userDAO.findById(saved2.getId()));
        assertNull(userDAO.findById(saved3.getId()));
        assertNull(userDAO.iFindById(saved.getId()));
        assertNull(userDAO.iFindById(saved2.getId()));
        assertNull(userDAO.iFindById(saved3.getId()));
    }

    @Test
    void testListarUsuarios() {
        userDAO.save(usuario);
        userDAO.save(usuario2);
        userDAO.save(usuario3);

        List<Usuario> lista = userDAO.findAll();
        assertEquals(3,lista.size());
    }
}
