package utn.tif.trabajo_integrador_final.utils;

import utn.tif.trabajo_integrador_final.entities.Usuario;
import utn.tif.trabajo_integrador_final.DTOs.UserRequestDTO;
import utn.tif.trabajo_integrador_final.DTOs.UserResponseDTO;

public class UserMapper {

    public static Usuario toEntity(UserRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        return usuario;
    }

    public static UserResponseDTO toResponse(Usuario usuario) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRoles(usuario.getRoles());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setModifiedAt(usuario.getModifiedAt());
        dto.setFechaNac(usuario.getFechaNac());
        return dto;
    }
}
