package utn.tif.trabajo_integrador_final.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.tif.trabajo_integrador_final.DTOs.UserRequestDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserResponseDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserUpdateDTO;
import utn.tif.trabajo_integrador_final.Services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        try {
            UserResponseDTO createdUser = userService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    // BULK CREATE
    @PostMapping("/bulk")
    public ResponseEntity<List<UserResponseDTO>> bulkCreate (@Valid @RequestBody List<UserRequestDTO> DTOS) {
        try {
            List<UserResponseDTO> createdUsers = userService.bulkCreateUsers(DTOS);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // READ MANY
    @GetMapping("/many")
    public ResponseEntity<List<UserResponseDTO>> getUsers(@Valid @RequestBody List<String> ids) {
        List<UserResponseDTO> users = userService.findManyUsers(ids);
        return ResponseEntity.ok(users);
    }
    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        try {
            UserResponseDTO user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE ONE
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @RequestBody UserUpdateDTO dto
    ) {
        try {
            UserResponseDTO updated = userService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // UPDATE MANY
    @PutMapping("/batch")
    public ResponseEntity<List<UserResponseDTO>> updateManyUsers(@RequestBody List<UserUpdateDTO> dtos) {
        try {
            List<UserResponseDTO> updatedUsers = userService.updateMany(dtos);
            return ResponseEntity.ok(updatedUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //BASIC AUTH
    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login( @RequestBody Map<String, String> credentials) {
        try{
            String email = credentials.get("email");
            String password = credentials.get("password");
            HashMap<String, Object> response = userService.checkAuth(email, password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // DELETE ONE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE MANY
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteManyUsers(@RequestBody List<String> ids) {
        try {
            userService.deleteMany(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/abs/{id}")
    public ResponseEntity<Void> totalDeleteUser(@PathVariable String id) {
        try {
            userService.totalDeleteOne(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/abs")
    public ResponseEntity<Void> totalDeleteManyUsers(@RequestBody List<String> ids) {
        try {
            userService.totalDeleteMany(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
