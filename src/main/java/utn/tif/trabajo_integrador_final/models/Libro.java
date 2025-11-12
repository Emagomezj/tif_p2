package utn.tif.trabajo_integrador_final.models;

public class Libro {
    private String id;
    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioEdicion;
    private String clasificacionDewey;
    private String estanteria;
    private String idioma;
    private Integer existencias;
    private Integer disponibles;
    private Boolean eliminado;

    public Libro() {}

    public Libro(String id, String isbn, String titulo, String autor, String editorial,
                 Integer anioEdicion, String clasificacionDewey, String estanteria,
                 String idioma, Integer existencias, Integer disponibles, Boolean eliminado) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioEdicion = anioEdicion;
        this.clasificacionDewey = clasificacionDewey;
        this.estanteria = estanteria;
        this.idioma = idioma;
        this.existencias = existencias;
        this.disponibles = disponibles;
        this.eliminado = eliminado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getAnioEdicion() { return anioEdicion; }
    public void setAnioEdicion(Integer anioEdicion) { this.anioEdicion = anioEdicion; }

    public String getClasificacionDewey() { return clasificacionDewey; }
    public void setClasificacionDewey(String clasificacionDewey) { this.clasificacionDewey = clasificacionDewey; }

    public String getEstanteria() { return estanteria; }
    public void setEstanteria(String estanteria) { this.estanteria = estanteria; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public Integer getExistencias() { return existencias; }
    public void setExistencias(Integer existencias) { this.existencias = existencias; }

    public Integer getDisponibles() { return disponibles; }
    public void setDisponibles(Integer disponibles) { this.disponibles = disponibles; }

    public Boolean getEliminado() { return eliminado; }
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        return "Libro{id='" + id + "', titulo='" + titulo + "', autor='" + autor + "'}";
    }

    public static class Builder {
        private final Libro libro;
        public Builder() {
            this.libro = new Libro();
            libro.setEliminado(false);
        }

        public Builder id(String id) { libro.setId(id); return this; }
        public Builder isbn(String isbn) { libro.setIsbn(isbn); return this; }
        public Builder titulo(String titulo) { libro.setTitulo(titulo); return this; }
        public Builder autor(String autor) { libro.setAutor(autor); return this; }
        public Builder editorial(String editorial) { libro.setEditorial(editorial); return this; }
        public Builder anioEdicion(Integer anioEdicion) { libro.setAnioEdicion(anioEdicion); return this; }
        public Builder clasificacionDewey(String c) { libro.setClasificacionDewey(c); return this; }
        public Builder estanteria(String e) { libro.setEstanteria(e); return this; }
        public Builder idioma(String idioma) { libro.setIdioma(idioma); return this; }
        public Builder existencias(Integer e) { libro.setExistencias(e); return this; }
        public Builder disponibles(Integer d) { libro.setDisponibles(d); return this; }
        public Builder eliminado(Boolean e) { libro.setEliminado(e); return this; }

        public Libro build() { return libro; }
    }
}
