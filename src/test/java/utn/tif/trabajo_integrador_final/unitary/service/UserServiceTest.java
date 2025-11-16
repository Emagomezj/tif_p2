package utn.tif.trabajo_integrador_final.unitary.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import utn.tif.trabajo_integrador_final.DAOS.UserDAO;
import utn.tif.trabajo_integrador_final.DTOs.UserRequestDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserResponseDTO;
import utn.tif.trabajo_integrador_final.Services.UserService;
import utn.tif.trabajo_integrador_final.constants.Roles;
import utn.tif.trabajo_integrador_final.entities.Usuario;
import utn.tif.trabajo_integrador_final.utils.Hasher;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private Hasher hasher;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO user;
    private UserRequestDTO user2;
    private UserRequestDTO user3;

    @BeforeEach
    void setUp() {
        user = new UserRequestDTO();
        user.setNombre("Pepe");
        user.setApellido("Pepito");
        user.setEmail("prueba@prueba.com");
        user.setPassword("12345678");
        user.setFechaNac(LocalDate.of(1990, 1, 1));
        ArrayList<String> roles = new ArrayList<>(Arrays.asList(Roles.USER.name(), Roles.ADMIN.name()));
        user.setRoles(roles);

        user2 = new UserRequestDTO();
        user2.setNombre("Pablo");
        user2.setApellido("Pablito");
        user2.setEmail("pablo@pablito.com");
        user2.setPassword("12345678");
        user2.setFechaNac(LocalDate.of(1990, 1, 1));

        user3 = new UserRequestDTO();
        user3.setNombre("Paula");
        user3.setApellido("Paulita");
        user3.setEmail("paula@paulita.com");
        user3.setPassword("12345678");
        user3.setFechaNac(LocalDate.of(1890, 1, 1));
    }

    @Test
    void testCrearUsuario() {
        when(hasher.hash("12345678")).thenReturn("HASH_1234");

        when(userDAO.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userService.create(user);
        response.getRoles();
        assertNotNull(response);
        assertEquals("Pepe", response.getNombre());
        assertEquals("Pepito", response.getApellido());
        assertEquals("prueba@prueba.com", response.getEmail());
        assertEquals(List.of(Roles.USER.name()), response.getRoles());

        verify(hasher).hash("12345678");


        verify(userDAO).save(argThat(user ->
                user.getEmail().equals("prueba@prueba.com")
                        && user.getHash().equals("HASH_1234")
                        && user.getRoles().contains(Roles.USER.name())
        ));
        assertThrows(IllegalArgumentException.class, () -> userService.create(user3));
    }

    @Test
    void testCreacionMultipleUsuario(){
        when(hasher.hash("12345678")).thenReturn("HASH_1234");
        when(userDAO.bulkCreate(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ArrayList<UserRequestDTO> list = new ArrayList<>(Arrays.asList(
                user,user2
        ));

        List<UserResponseDTO> response = userService.bulkCreateUsers(list);
        assertEquals(list.size(), response.size());
        verify(hasher, times(2)).hash(any());
        verify(userDAO, times(1)).bulkCreate(any());
    }


}
