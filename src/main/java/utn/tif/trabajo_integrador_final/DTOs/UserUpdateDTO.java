package utn.tif.trabajo_integrador_final.DTOs;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UserUpdateDTO {
    private String id;
    private Optional<String> nombre = Optional.empty();
    private Optional<String> apellido = Optional.empty();
    private Optional<String> email = Optional.empty();
    private Optional<String> password = Optional.empty();
    private Optional<LocalDate> fechaNac = Optional.empty();
    private Optional<List<String>> roles = Optional.empty();
    private Optional<Boolean> eliminado  = Optional.empty();


    public UserUpdateDTO() {}

    public Optional<Boolean> getEliminado() {
        return eliminado;
    }
    public void setEliminado(Boolean eliminado) {
        this.eliminado = Optional.ofNullable(eliminado);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Optional<List<String>> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = Optional.of(roles);
    }

    public Optional<String> getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Optional.ofNullable(nombre);
    }

    public Optional<String> getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = Optional.ofNullable(apellido);
    }

    public Optional<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Optional.ofNullable(email);
    }

    public Optional<String > getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Optional.ofNullable(password);
    }

    public Optional<LocalDate> getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = Optional.ofNullable(fechaNac);
    }
}
