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
import utn.tif.trabajo_integrador_final.DTOs.UserRequestDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserResponseDTO;
import utn.tif.trabajo_integrador_final.Services.LibroService;
import utn.tif.trabajo_integrador_final.Services.PrestamoService;
import utn.tif.trabajo_integrador_final.Services.UserService;
import utn.tif.trabajo_integrador_final.entities.Libro;
import utn.tif.trabajo_integrador_final.entities.Prestamo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

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
public class PrestamoControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    LibroService libroService;
    @Autowired
    PrestamoService prestamoService;
    @Autowired
    DataSource dataSource;

    private String uid;
    private String lid;

    @BeforeEach
    void init() throws Exception{
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()){
            st.execute("DELETE FROM prestamos");
            st.execute("DELETE FROM libros");
            st.execute("DELETE FROM usuarios");
        }
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setEmail("juan@mail.com");
        dto.setPassword("12345678");
        dto.setFechaNac(LocalDate.of(1990,1,1));
        UserResponseDTO userCreated = userService.create(dto);
        uid = userCreated.getId();

        Libro libro = new Libro(
                "L1", "978-123-1237", "Test Book",
                "Autor", "Editorial", 2020,
                "005", "A1", "esp", 3, 2, false
        );
        Libro libroCreated = libroService.createLibro(libro);
        lid = libroCreated.getId();
    }



    @Test
    void testCrearPrestamo() throws Exception {
        var user = userService.findAll().get(0);
        var libro = libroService.getAll().get(0);

        String json = """
        {
            "userId":"%s",
            "libroId":"%s",
            "fechaPrestamo":"2025-01-01"
        }
        """.formatted(user.getId(), libro.getId());

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    void testBuscarPrestamosPorUsuario() throws Exception {
        var user = userService.findAll().get(0);

        mockMvc.perform(get("/api/prestamos/user/" + user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarPrestamo() throws Exception {
        var p = prestamoService.createPrestamo(new Prestamo(null,uid,lid,
                LocalDate.now(), LocalDate.now().plusDays(10), null, "ACTIVO"));

        mockMvc.perform(delete("/api/prestamos/" + p.getId()))
                .andExpect(status().isOk());
    }
}

