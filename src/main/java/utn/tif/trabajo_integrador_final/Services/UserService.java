package utn.tif.trabajo_integrador_final.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utn.tif.trabajo_integrador_final.DAOS.UserDAO;
import utn.tif.trabajo_integrador_final.DTOs.UserRequestDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserResponseDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserUpdateDTO;
import utn.tif.trabajo_integrador_final.exceptions.EntityNotFoundException;
import utn.tif.trabajo_integrador_final.models.Usuario;
import utn.tif.trabajo_integrador_final.utils.Hasher;
import utn.tif.trabajo_integrador_final.utils.Id_generator;
import utn.tif.trabajo_integrador_final.utils.UserMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class UserService {
    private final UserDAO userDAO;
    private final Hasher hasher;

    @Autowired
    public UserService(UserDAO userDAO, Hasher hasher) {
        this.userDAO = userDAO;
        this.hasher = hasher;
    }

    private Usuario convertDTOToEntity_Create(UserRequestDTO dto) {
        Usuario user = new Usuario();
        user.setId(Id_generator.generarId());
        user.setNombre(dto.getNombre());
        user.setApellido(dto.getApellido());
        user.setEmail(dto.getEmail());
        user.setHash(hasher.hash(dto.getPassword()));
        user.setFechaNac(dto.getFechaNac());
        user.setRoles(List.of("USER"));
        user.setEliminado(false);
        user.setCreatedAt(LocalDate.now());
        user.setModifiedAt(LocalDate.now());
        user.setPassLastMod(LocalDate.now());
        return user;
    }
    public UserResponseDTO create(UserRequestDTO dto) throws Exception {
        Usuario user = convertDTOToEntity_Create(dto);

        Usuario response = userDAO.save(user);
        return UserMapper.toResponse(response);
    }
    public List<UserResponseDTO> bulkCreateUsers(List<UserRequestDTO> userDTOs) {
        try{
            List<Usuario> users = new ArrayList<>();
            for (UserRequestDTO dto : userDTOs) {
                Usuario user = convertDTOToEntity_Create(dto);
                users.add(user);
            }
            List<Usuario> raw_response = userDAO.bulkCreate(users);
            List<UserResponseDTO> response = new ArrayList<>();
            for (Usuario user : raw_response) {
                response.add(UserMapper.toResponse(user));
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear usuarios",e);
        }
    }
    public UserResponseDTO findById(String id) throws Exception {
        Usuario user = userDAO.findById(id);
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        return UserMapper.toResponse(user);
    }
    public List<UserResponseDTO> findAll() {
        try{
            List<Usuario> users = userDAO.findAll();
            //System.out.println(users.stream().findFirst());
            List<UserResponseDTO> response = new ArrayList<>();
            for (Usuario user : users) {
                response.add(UserMapper.toResponse(user));
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuarios",e);
        }
    }
    public List<UserResponseDTO> findManyUsers(List<String> ids) {
        try{
            List<Usuario> users = userDAO.findMany(ids);
            List<UserResponseDTO> response = new ArrayList<>();
            for (Usuario user : users) {
                response.add(UserMapper.toResponse(user));
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuarios",e);
        }
    }
    public HashMap<String, Object> checkAuth(String email, String password) throws Exception{
        try{
            Usuario user = userDAO.findByEmail(email);
            if (user == null){
                throw new Exception("User not found");
            }
            if (hasher.checkHash(password, user.getHash())) {
                HashMap<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("user_id", user.getId());
                response.put("name", user.getNombre());
                response.put("surname", user.getApellido());
                response.put("email", user.getEmail());
                response.put("passLastMod", user.getPassLastMod());
                response.put("roles",  user.getRoles());
                response.put("message", "Authentication successful");
                if(ChronoUnit.DAYS.between(user.getPassLastMod(), LocalDate.now()) >= 30){
                    response.put("SecurityAdvice","Han pasado más de 30 días desde la última modificación de su contraseña, debe cambiarla" );
                }
                return response;
            } else {
                HashMap<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid password");
                return response;
            }
        } catch (Exception e) {
            throw new Exception("Authentication failed: " + e.getMessage());
        }
    }
    public UserResponseDTO update(String id, UserUpdateDTO dto) throws Exception {
        Usuario user = userDAO.findById(id);
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        dto.getNombre().ifPresent(user::setNombre);
        dto.getApellido().ifPresent(user::setApellido);
        dto.getEmail().ifPresent(user::setEmail);
        dto.getRoles().ifPresent(user::setRoles);
        dto.getEliminado().ifPresent(user::setEliminado);
        dto.getPassword().ifPresent(pwd -> user.setHash(hasher.hash(pwd)));
        user.setModifiedAt(LocalDate.now());
        Usuario response = userDAO.updateOne(user);
        return UserMapper.toResponse(response);
    }
    public List<UserResponseDTO> updateMany(List<UserUpdateDTO> dtos) throws Exception {
        List<Usuario> users = new ArrayList<>();
        for (UserUpdateDTO dto: dtos){
            Usuario user = userDAO.findById(dto.getId());
            if (user == null){
                throw new EntityNotFoundException("User not found");
            }
            dto.getNombre().ifPresent(user::setNombre);
            dto.getApellido().ifPresent(user::setApellido);
            dto.getEmail().ifPresent(user::setEmail);
            dto.getRoles().ifPresent(user::setRoles);
            dto.getPassword().ifPresent(pwd -> user.setHash(hasher.hash(pwd)));
            user.setModifiedAt(LocalDate.now());
            users.add(user);
        }
        List<Usuario> rawResponse = userDAO.updateMany(users);
        List<UserResponseDTO> response = new ArrayList<>();
        for (Usuario user: rawResponse){
            UserResponseDTO userResponseDTO = UserMapper.toResponse(user);
            response.add(userResponseDTO);
        }
        return response;
    }
    public void deleteById(String id) throws Exception {
        Usuario user = userDAO.findById(id);
        if (user == null){
            throw new EntityNotFoundException("User not found");
        }
        userDAO.deleteOne(id);
    }
    public void deleteMany(List<String> ids) throws Exception {
        List<Usuario> users = userDAO.findMany(ids);
        userDAO.deleteMany(ids);
    }

    public void totalDeleteOne(String id) throws Exception {
        userDAO.totalDeleteOne(id);
    }
    public void totalDeleteMany(List<String> ids) throws Exception {
        userDAO.totalDeleteMany(ids);
    }
}
