package utn.tif.trabajo_integrador_final.unitary.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utn.tif.trabajo_integrador_final.DAOS.LibroDAO;
import utn.tif.trabajo_integrador_final.DAOS.PrestamoDAO;
import utn.tif.trabajo_integrador_final.DAOS.UserDAO;
import utn.tif.trabajo_integrador_final.constants.EstadoPrestamo;
import utn.tif.trabajo_integrador_final.constants.Roles;
import utn.tif.trabajo_integrador_final.entities.Libro;
import utn.tif.trabajo_integrador_final.entities.Prestamo;
import utn.tif.trabajo_integrador_final.entities.Usuario;
import utn.tif.trabajo_integrador_final.utils.Id_generator;

import javax.sql.DataSource;
import java.sql.Connection;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrestamoDAOTest {
    @Autowired
    private PrestamoDAO prestamoDAO;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LibroDAO libroDAO;

    private Prestamo prestamo;
    private Prestamo prestamo2;
    private Prestamo prestamo3;

    @BeforeAll
    void cargarUsuarios(){

       Usuario usuario = new Usuario();
        usuario.setId("u1");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juanperez@example.com");
        usuario.setHash("contraseña123");
        usuario.setFechaNac(LocalDate.of(1990, 1, 1));
        ArrayList<String> roles = new ArrayList<>();
        roles.add(Roles.USER.name());
        roles.add(Roles.ADMIN.name());
        usuario.setRoles(roles);
        usuario.setModifiedAt(LocalDate.now());
        usuario.setCreatedAt(LocalDate.now());
        usuario.setPassLastMod(LocalDate.now());

        Usuario usuario2 = new Usuario();
        usuario2.setId("u2");
        usuario2.setNombre("María");
        usuario2.setApellido("Gómez");
        usuario2.setFechaNac(LocalDate.of(1990, 1, 1));
        usuario2.setEmail("mariagomez@example.com");
        usuario2.setHash("5678");
        usuario2.setModifiedAt(LocalDate.now());
        usuario2.setCreatedAt(LocalDate.now());
        usuario2.setPassLastMod(LocalDate.now());

        Usuario usuario3 = new Usuario();
        usuario3.setId("u3");
        usuario3.setNombre("Carlos");
        usuario3.setApellido("López");
        usuario3.setEmail("carloslopez@example.com");
        usuario3.setFechaNac(LocalDate.of(1990, 1, 1));
        usuario3.setHash("9012");
        usuario3.setModifiedAt(LocalDate.now());
        usuario3.setCreatedAt(LocalDate.now());
        usuario3.setPassLastMod(LocalDate.now());
        if(userDAO.findById(usuario.getId()) == null){
            userDAO.save(usuario);
        } else {
            userDAO.updateOne(usuario);
        }
        if(userDAO.findById(usuario2.getId()) == null){
            userDAO.save(usuario2);
        } else {
            userDAO.updateOne(usuario2);
        }
        if(userDAO.findById(usuario3.getId()) == null){
            userDAO.save(usuario3);
        } else {
            userDAO.updateOne(usuario3);
        }
    }

    @BeforeAll
    void cargarLibros(){
        Libro libro = new Libro(
                "l1",
                "978-0134685991",
                "Effective Java",
                "Joshua Bloch",
                "Addison-Wesley",
                2018,
                "005.133",
                "PROG-01",
                "inglés",
                3,
                2,
                false
        );

        Libro libro2 = new Libro(
                "l2",
                "978-0141439518",
                "Frankenstein",
                "Mary Shelley",
                "Penguin Classics",
                2003,
                "823.7",
                "CLAS-01",
                "inglés",
                2,
                1,
                false
        );

        Libro libro3 = new Libro(
                "l3",
                "978-8426418197",
                "El Quijote",
                "Miguel de Cervantes",
                "Penguin Clásicos",
                2015,
                "863",
                "CLAS-02",
                "español",
                6,
                4,
                false
        );

        if(libroDAO.findById(libro.getId()) == null){
            libroDAO.save(libro);
        } else {
            libroDAO.updateOne(libro);
        }
        if(libroDAO.findById(libro2.getId()) == null){
            libroDAO.save(libro2);
        } else {
            libroDAO.updateOne(libro2);
        }
        if(libroDAO.findById(libro3.getId()) == null){
            libroDAO.save(libro3);
        } else {
            libroDAO.updateOne(libro3);
        }
    }

    @BeforeEach
    public void setUp() {
        prestamo = new Prestamo();
        prestamo.setUserId("u1");
        prestamo.setLibroId("l1");
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaPlazo(LocalDate.of(2025,12,30));
        prestamo.setFechaDevolucion(null);
        prestamo.setEstado(EstadoPrestamo.ACTIVO.getValue());

        prestamo2 = new Prestamo();
        prestamo2.setUserId("u2");
        prestamo2.setLibroId("l2");
        prestamo2.setFechaPrestamo(LocalDate.now());
        prestamo2.setFechaPlazo(LocalDate.of(2025,10,30));
        prestamo2.setFechaDevolucion(null);
        prestamo2.setEstado(EstadoPrestamo.ACTIVO.getValue());

        prestamo3 = new Prestamo();
        prestamo3.setUserId("u3");
        prestamo3.setLibroId("l3");
        prestamo3.setFechaPrestamo(LocalDate.now());
        prestamo3.setFechaPlazo(LocalDate.of(2025,10,30));
        prestamo3.setFechaDevolucion(LocalDate.of(2025,11,10));
        prestamo3.setEstado(EstadoPrestamo.DEVUELTO.getValue());
    }

    @BeforeEach
    void limpiar() throws Exception{
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()){
            st.execute("DELETE FROM prestamos");
            //st.execute("DELETE FROM libros");
            //st.execute("DELETE FROM usuarios");
        }
    }

    @AfterAll
    void limpiarFinal() throws Exception{
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()){
            st.execute("DELETE FROM prestamos");
            st.execute("DELETE FROM libros");
            st.execute("DELETE FROM usuarios");
        }
    }

    @Test
    void testCrearPrestamo() {
        Prestamo found = prestamoDAO.save(prestamo);
        assertNotNull(found);
        assertNotNull(found.getId());
    }

    @Test
    void testCrearVariosPrestamo() {
        ArrayList<Prestamo> prestamos = new ArrayList<>(Arrays.asList(
                prestamo,prestamo2,prestamo3
        ));
        List<Prestamo> created = prestamoDAO.bulkCreate(prestamos);
        assertEquals(prestamos.size(), created.size());
    }

    @Test
    void testBuscarPrestamo() {
        Prestamo created = prestamoDAO.save(prestamo);
        Prestamo found = prestamoDAO.findById(String.valueOf(created.getId()));
        assertEquals(created.getId(), found.getId());
        assertNotNull(found);
    }

    @Test
    void testBuscarVariosPrestamo() {
        Prestamo created = prestamoDAO.save(prestamo);
        Prestamo created2 = prestamoDAO.save(prestamo2);
        Prestamo created3 = prestamoDAO.save(prestamo3);
        ArrayList<String > ids= new ArrayList<>(Arrays.asList(
                String.valueOf(created.getId()),String.valueOf(created2.getId()),String.valueOf(created3.getId())
        ));
        List<Prestamo> found = prestamoDAO.findMany(ids);
        assertEquals(created.getId(), found.get(0).getId());
        assertEquals(created2.getLibroId(), found.get(1).getLibroId());
        assertEquals(created3.getUserId(), found.get(2).getUserId());
    }

    @Test
    void testBuscarPorUsuario() {
        Prestamo created = prestamoDAO.save(prestamo);
        List<Prestamo> found = prestamoDAO.findByUserId("u1");
        assertEquals(created.getId(), found.get(0).getId());
    }

    @Test
    void testBuscarPorLibro() {
        Prestamo created = prestamoDAO.save(prestamo);
        List<Prestamo> found = prestamoDAO.findByLibroId("l1");
    }

    @Test
    void testBuscarPorEstado() {
        Prestamo created = prestamoDAO.save(prestamo);
        List<Prestamo> found = prestamoDAO.findByEstado(EstadoPrestamo.ACTIVO.getValue());
        assertTrue(found.size() > 0);
    }

    @Test
    void testActualizarPrestamo() {
        Prestamo created = prestamoDAO.save(prestamo);
        prestamo.setEstado(EstadoPrestamo.DEVUELTO.getValue());
        Prestamo updated = prestamoDAO.updateOne(prestamo);
        assertEquals(prestamo, updated);
        assertEquals(EstadoPrestamo.DEVUELTO.getValue(), prestamo.getEstado());
    }

    @Test
    void testActualizarVariosPrestamo() {
        ArrayList<Prestamo> prestamos = new ArrayList<>(Arrays.asList(
                prestamo,prestamo2,prestamo3
        ));
        List<Prestamo> created = prestamoDAO.bulkCreate(prestamos);
        prestamo.setEstado(EstadoPrestamo.DEVUELTO.getValue());
        prestamo2.setEstado(EstadoPrestamo.DEVUELTO.getValue());
        prestamo3.setEstado(EstadoPrestamo.DEVUELTO.getValue());
        List<Prestamo> updated = prestamoDAO.updateMany(prestamos);
        assertEquals(prestamo, updated.get(0));
        assertEquals(prestamo2, updated.get(1));
        assertEquals(prestamo3, updated.get(2));
    }

    @Test
    void testEliminarPrestamo() {
        Prestamo created = prestamoDAO.save(prestamo);
        prestamoDAO.deleteOne(String.valueOf(created.getId()));
        Prestamo found = prestamoDAO.findById(String.valueOf(created.getId()));
        assertNull(found);
    }

    @Test
    void testEncontrarPrestamoVencido() {
        ArrayList<Prestamo> prestamos = new ArrayList<>(Arrays.asList(
                prestamo,prestamo2,prestamo3
        ));
        List<Prestamo> created = prestamoDAO.bulkCreate(prestamos);
        List<Prestamo> found = prestamoDAO.findPrestamosVencidos_update();
        assertEquals(prestamo2.getId(), found.get(0).getId());
    }
}
