package utn.tif.trabajo_integrador_final.entities;

import utn.tif.trabajo_integrador_final.constants.EstadoPrestamo;

import java.time.LocalDate;

public class Prestamo {
    private Integer id;
    private String userId;
    private String libroId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaPlazo;
    private LocalDate fechaDevolucion;
    private String estado; // activo, devuelto, vencido

    public Prestamo() {}

    public Prestamo(Integer id, String userId, String libroId, LocalDate fechaPrestamo,
                    LocalDate fechaPlazo, LocalDate fechaDevolucion, String estado) {
        this.id = id;
        this.userId = userId;
        this.libroId = libroId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaPlazo = fechaPlazo;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLibroId() { return libroId; }
    public void setLibroId(String libroId) { this.libroId = libroId; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaPlazo() { return fechaPlazo; }
    public void setFechaPlazo(LocalDate fechaPlazo) { this.fechaPlazo = fechaPlazo; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Prestamo{id=" + id + ", userId='" + userId + "', libroId='" + libroId + "', estado='" + estado + "'}";
    }

    public static class Builder {
        private final Prestamo prestamo;

        public Builder() {
            this.prestamo = new Prestamo();
            this.prestamo.setEstado(EstadoPrestamo.ACTIVO.getValue());
        }

        public Builder id(Integer id) { prestamo.setId(id); return this; }
        public Builder userId(String userId) { prestamo.setUserId(userId); return this; }
        public Builder libroId(String libroId) { prestamo.setLibroId(libroId); return this; }
        public Builder fechaPrestamo(LocalDate fechaPrestamo) { prestamo.setFechaPrestamo(fechaPrestamo); return this; }
        public Builder fechaPlazo(LocalDate fechaPlazo) { prestamo.setFechaPlazo(fechaPlazo); return this; }
        public Builder fechaDevolucion(LocalDate fechaDevolucion) { prestamo.setFechaDevolucion(fechaDevolucion); return this; }
        public Builder estado(String estado) { prestamo.setEstado(estado); return this; }

        public Prestamo build() { return prestamo; }
    }
}
