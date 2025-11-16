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
import utn.tif.trabajo_integrador_final.Services.UserService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void prepararDatos() throws Exception {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {

            st.execute("DELETE FROM roles_usuario");
            st.execute("DELETE FROM usuarios");
        }

        UserRequestDTO dto = new UserRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPassword("12345678");
        dto.setFechaNac(LocalDate.of(1990,1,1));

        userService.create(dto);
    }

    @Test
    void testCrearUsuario() throws Exception {
        String json = """
        {
            "nombre": "Maria",
            "apellido": "Gomez",
            "email": "maria@example.com",
            "password": "87654321",
            "fechaNac": "1995-08-10"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@example.com"));
    }

    @Test
    void testEmailErroneo() throws Exception {
        String json = """
        {
            "nombre": "Maria",
            "apellido": "Gomez",
            "email": null,
            "password": "87654321",
            "fechaNac": "1995-08-10"
        }
        """;
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById() throws Exception {
        var user = userService.findAll().get(0);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testLoginCorrecto() throws Exception {
        String json = """
        {
            "email": "juan@example.com",
            "password": "12345678"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLoginErroneo() throws Exception {
        String json = """
        {
            "email": "juan@example.com",
            "password": "12345"
        }
        """;
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginIncorrecto() throws Exception {
        String json = """
        {
            "email": "juan@example.com",
            "password": "wrongpassword"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}
