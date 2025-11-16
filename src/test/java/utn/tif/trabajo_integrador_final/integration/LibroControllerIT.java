package utn.tif.trabajo_integrador_final.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import utn.tif.trabajo_integrador_final.Services.LibroService;
import utn.tif.trabajo_integrador_final.entities.Libro;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LibroControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    LibroService libroService;
    @Autowired
    DataSource dataSource;

    private String lid;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()){
            //st.execute("DELETE FROM prestamos");
            st.execute("DELETE FROM libros");
            //st.execute("DELETE FROM usuarios");
        }
        Libro libro = new Libro(
                null, "978-0134685991", "Effective Java",
                "Joshua Bloch", "AW", 2018, "005.133",
                "PROG-01", "ingles", 5, 4, false
        );
        Libro createdLibro = libroService.createLibro(libro);
        lid = createdLibro.getId();
    }

    @Test
    void testCrearLibro() throws Exception {
        String json = """
        {
            "id":"L2",
            "isbn":"978-0143127796",
            "titulo":"Clean Code",
            "autor":"Robert Martin",
            "editorial":"Prentice Hall",
            "anioEdicion":2009,
            "clasificacionDewey":"005.1",
            "estanteria":"PROG-02",
            "idioma":"ingles",
            "existencias":4,
            "disponibles":4,
            "eliminado":false
        }
        """;

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void testAnioErroneo() throws Exception {
        String json = """
        {
            "isbn":"978-0143127797",
            "titulo":"Clean Code",
            "autor":"Robert Martin",
            "editorial":"Prentice Hall",
            "anioEdicion":2026,
            "clasificacionDewey":"005.1",
            "estanteria":"PROG-02",
            "idioma":"ingles",
            "existencias":4,
            "disponibles":4,
            "eliminado":false
        }
        """;
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIsbnErroneo() throws Exception {
        String json = """
        {
            "isbn":"978",
            "titulo":"Clean Code",
            "autor":"Robert Martin",
            "editorial":"Prentice Hall",
            "anioEdicion":2026,
            "clasificacionDewey":"005.1",
            "estanteria":"PROG-02",
            "idioma":"ingles",
            "existencias":4,
            "disponibles":4,
            "eliminado":false
        }
        """;
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExistenciasNula() throws Exception {
        String json = """
        {
            "isbn":"9781234567",
            "titulo":"Clean Code",
            "autor":"Robert Martin",
            "editorial":"Prentice Hall",
            "anioEdicion":2026,
            "clasificacionDewey":"005.1",
            "estanteria":"PROG-02",
            "idioma":"ingles",
            "existencias":null,
            "disponibles":null,
            "eliminado":false
        }
        """;

        mockMvc.perform(post("/api/libros"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllLibros() throws Exception {
        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetLibroById() throws Exception {
        mockMvc.perform(get("/api/libros/"+lid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Effective Java"));
    }
}

