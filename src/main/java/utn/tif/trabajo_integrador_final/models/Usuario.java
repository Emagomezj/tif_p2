package utn.tif.trabajo_integrador_final.models;

import utn.tif.trabajo_integrador_final.constants.Roles;
import utn.tif.trabajo_integrador_final.utils.Id_generator;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNac;
    private String hash;
    private Boolean eliminado;
    private LocalDate createdAt;
    private LocalDate modifiedAt;
    private LocalDate passLastMod;
    private List<String> roles; // viene de roles_usuario

    public Usuario() {
        this.roles = new ArrayList<>();
        this.eliminado = false;
    }

    public Usuario(String id, String nombre, String apellido, String email, LocalDate fechaNac,
                   String hash, Boolean eliminado, LocalDate createdAt, LocalDate modifiedAt,
                   LocalDate passLastMod, List<String> roles) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNac = fechaNac;
        this.hash = hash;
        this.eliminado = eliminado;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.passLastMod = passLastMod;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaNac() { return fechaNac; }
    public void setFechaNac(LocalDate fechaNac) { this.fechaNac = fechaNac; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public Boolean getEliminado() { return eliminado; }
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDate modifiedAt) { this.modifiedAt = modifiedAt; }

    public LocalDate getPassLastMod() { return passLastMod; }
    public void setPassLastMod(LocalDate passLastMod) { this.passLastMod = passLastMod; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", fechaNac=" + fechaNac +
                ", hash='" + hash + '\'' +
                ", eliminado=" + eliminado +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", passLastMod=" + passLastMod +
                ", roles=" + roles +
                '}';
    }

    public static class Builder {
        private final Usuario usuario;

        public Builder() {
            this.usuario = new Usuario();
            this.usuario.setEliminado(false);
            List<String> defaultRole = new ArrayList<>();
            defaultRole.add(Roles.USER.name());
            this.usuario.setRoles(defaultRole);
            this.usuario.setId(Id_generator.generarId());
            this.usuario.setCreatedAt(LocalDate.now());
        }

        public Builder id(String id) { usuario.setId(id); return this; }
        public Builder nombre(String nombre) { usuario.setNombre(nombre); return this; }
        public Builder apellido(String apellido) { usuario.setApellido(apellido); return this; }
        public Builder email(String email) { usuario.setEmail(email); return this; }
        public Builder fechaNac(LocalDate fechaNac) { usuario.setFechaNac(fechaNac); return this; }
        public Builder hash(String hash) { usuario.setHash(hash); return this; }
        public Builder eliminado(Boolean eliminado) { usuario.setEliminado(eliminado); return this; }
        public Builder createdAt(LocalDate createdAt) { usuario.setCreatedAt(createdAt); return this; }
        public Builder modifiedAt(LocalDate modifiedAt) { usuario.setModifiedAt(modifiedAt); return this; }
        public Builder passLastMod(LocalDate passLastMod) { usuario.setPassLastMod(passLastMod); return this; }
        public Builder roles(List<String> roles) { usuario.setRoles(roles); return this; }

        public Usuario build() { return usuario; }
    }
}
