package utn.tif.trabajo_integrador_final.unitary.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utn.tif.trabajo_integrador_final.DAOS.LibroDAO;
import utn.tif.trabajo_integrador_final.entities.Libro;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibroDAOTest {
    @Autowired
    private LibroDAO libroDAO;
    private Libro libro;
    private Libro libro2;
    private Libro libro3;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        libro = new Libro(
                "L001",
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

        libro2 = new Libro(
                "L002",
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

        libro3 = new Libro(
                "L003",
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

    @BeforeEach
    void limpiar() throws Exception {
        try(Connection c = dataSource.getConnection()) {
            Statement st = c.createStatement();
            st.execute("DELETE FROM libros");
        }
    }
    @Test
    void testCrearLibro() {
        Libro saved = libroDAO.save(libro);
        libro2.setId(saved.getId());
        assertNotNull(saved);
        assertThrows(RuntimeException.class, () -> libroDAO.save(libro2));
    }

    @Test
    void testCreacionMultipleLibro() {
        ArrayList<Libro> libros = new ArrayList<>(Arrays.asList(
                libro, libro2, libro3
        ));
        List<Libro> saved = libroDAO.bulkCreate(libros);
        assertNotNull(saved);
        assertEquals(libros.size(), saved.size());
        Libro found = libroDAO.findById(saved.get(0).getId());
        Libro found2 = libroDAO.findById(saved.get(1).getId());
        Libro found3 = libroDAO.findById(saved.get(2).getId());
        assertNotNull(found);
        assertNotNull(found2);
        assertNotNull(found3);
        assertEquals(found.getId(), saved.get(0).getId());
        assertEquals(found2.getId(), saved.get(1).getId());
        assertEquals(found3.getId(), saved.get(2).getId());
    }

    @Test
    void testBuscarLibro() {
        Libro saved = libroDAO.save(libro);
        Libro found = libroDAO.findById(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void testBuscarVariosLibro() {
        ArrayList<Libro> libros = new ArrayList<>(Arrays.asList(
                libro, libro2, libro3
        ));
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(
                libro.getId(), libro2.getId(), libro3.getId()
        ));
        List<Libro> saved = libroDAO.bulkCreate(libros);
        List<Libro> found = libroDAO.findMany(ids);
        assertNotNull(found);
        assertEquals(saved.size(), found.size());
        assertEquals(3, found.size());
    }

    @Test
    void testActualizarLibro() {
        Libro saved = libroDAO.save(libro);
        libro.setEliminado(true);
        Libro updated = libroDAO.updateOne(libro);
        assertNotNull(updated);
        Libro found = libroDAO.findById(updated.getId());
        assertNull(found);
    }

    @Test
    void testActualizarVariosLibro() {
        ArrayList<Libro> libros = new ArrayList<>(Arrays.asList(
                libro, libro2, libro3
        ));
        List<Libro> saved = libroDAO.bulkCreate(libros);
        libro.setTitulo("PruebaTitulo");
        libro2.setExistencias(0);
        libro3.setAutor("PruebaAutor");
        List<Libro> updated = libroDAO.updateMany(libros);
        assertNotNull(updated);
        assertEquals(3, updated.size());
    }

    @Test
    void testEliminarLibro() {
        Libro saved = libroDAO.save(libro);
        libroDAO.deleteOne(saved.getId());
        assertNotNull(libroDAO.iFindById(saved.getId()));
        assertNull(libroDAO.findById(saved.getId()));
    }

    @Test
    void testEliminarVariosLibro() {
        libroDAO.save(libro);
        libroDAO.save(libro2);
        libroDAO.save(libro3);
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(
                libro.getId(), libro2.getId(), libro3.getId()
        ));
        libroDAO.deleteMany(ids);
        assertNotNull(libroDAO.iFindById(ids.get(0)));
        assertNotNull(libroDAO.iFindById(ids.get(1)));
        assertNotNull(libroDAO.iFindById(ids.get(2)));
        assertEquals(0, libroDAO.findMany(ids).size());
    }

    @Test
    void totalEliminacionIndividualLibro(){
        libroDAO.save(libro);
        libroDAO.totalDeleteOne(libro.getId());
        assertNull(libroDAO.iFindById(libro.getId()));
        assertNull(libroDAO.findById(libro.getId()));
    }

    @Test
    void totalEliminacionVariosLibro(){
        libroDAO.save(libro);
        libroDAO.save(libro2);
        libroDAO.save(libro3);
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(
                libro.getId(), libro2.getId(), libro3.getId()
        ));
        libroDAO.totalDeleteMany(ids);
        List<Libro> libros = libroDAO.findMany(ids);
        assertNull(libroDAO.iFindById(ids.get(0)));
        System.out.println(libros);
    }
}


