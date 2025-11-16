package utn.tif.trabajo_integrador_final.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utn.tif.trabajo_integrador_final.DAOS.LibroDAO;
import utn.tif.trabajo_integrador_final.exceptions.EntityNotFoundException;
import utn.tif.trabajo_integrador_final.entities.Libro;
import utn.tif.trabajo_integrador_final.utils.Id_generator;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class LibroService {

    private final LibroDAO libroDAO;

    @Autowired
    public LibroService(LibroDAO libroDAO) {
        this.libroDAO = libroDAO;
    }


    private void validateLibro(Libro libro, boolean isUpdate)  {
        if (libro == null)
            throw new IllegalArgumentException("El libro no puede ser nulo.");

        if (isNullOrEmpty(libro.getTitulo()))
            throw new IllegalArgumentException("El título del libro es obligatorio.");

        if (isNullOrEmpty(libro.getAutor()))
            throw new IllegalArgumentException("El autor del libro es obligatorio.");

        if (isNullOrEmpty(libro.getEditorial()))
            throw new IllegalArgumentException("La editorial es obligatoria.");

        if (libro.getAnioEdicion() != null && (libro.getAnioEdicion() > Year.now().getValue()))
            throw new IllegalArgumentException("El año de edición debe ser real.");

        if (libro.getExistencias() == null || libro.getExistencias() < 0)
            throw new IllegalArgumentException("Las existencias no pueden ser negativas ni nulas.");

        if (libro.getDisponibles() == null || libro.getDisponibles() < 0)
            throw new IllegalArgumentException("Los ejemplares disponibles no pueden ser negativos ni nulos.");

        if (libro.getDisponibles() > libro.getExistencias())
            throw new IllegalArgumentException("Los ejemplares disponibles no pueden superar las existencias totales.");

        if (!isNullOrEmpty(libro.getIsbn()) && !isValidIsbn(libro.getIsbn()))
            throw new IllegalArgumentException("El ISBN no es válido. Debe tener formato ISBN-10 o ISBN-13.");

        if (isUpdate && isNullOrEmpty(libro.getId()))
            throw new IllegalArgumentException("El ID del libro es obligatorio para actualizar.");
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidIsbn(String isbn) {
        String clean = isbn.replace("-", "").trim();
        return Pattern.matches("\\d{10}|\\d{13}", clean);
    }

    public Libro createLibro(Libro libro)  {
        libro.setId(Id_generator.generarId());
        validateLibro(libro, false);
        return libroDAO.save(libro);
    }

    public List<Libro> bulkCreateLibros(List<Libro> libros)  {
        if (libros == null || libros.isEmpty())
            throw new IllegalArgumentException("La lista de libros a crear no puede estar vacía.");

        for (Libro libro : libros) {
            libro.setId(Id_generator.generarId());
            validateLibro(libro, false);
        }
        return libroDAO.bulkCreate(libros);
    }

    public Optional<Libro> findById(String id)  {
        if (isNullOrEmpty(id))
            throw new IllegalArgumentException("El ID del libro no puede ser nulo o vacío.");

        return Optional.ofNullable(libroDAO.findById(id));
    }

    public List<Libro> getAll()  {
        return libroDAO.findAll();
    }

    public List<Libro> findManyById(List<String> ids)  {
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("La lista de IDs no puede estar vacía.");

        return libroDAO.findMany(ids);
    }

    public Libro updateLibro(Libro libro)  {
        if (isNullOrEmpty(libro.getId()))
            throw new IllegalArgumentException("El ID del libro es obligatorio para actualizar.");

        Libro libroActual = libroDAO.findById(libro.getId());
        if (libroActual == null)
            throw new EntityNotFoundException("No se encontró un libro con el ID proporcionado.");

        if (!isNullOrEmpty(libro.getIsbn())) libroActual.setIsbn(libro.getIsbn());
        if (!isNullOrEmpty(libro.getTitulo())) libroActual.setTitulo(libro.getTitulo());
        if (!isNullOrEmpty(libro.getAutor())) libroActual.setAutor(libro.getAutor());
        if (!isNullOrEmpty(libro.getEditorial())) libroActual.setEditorial(libro.getEditorial());
        if (libro.getAnioEdicion() != null) libroActual.setAnioEdicion(libro.getAnioEdicion());
        if (!isNullOrEmpty(libro.getClasificacionDewey())) libroActual.setClasificacionDewey(libro.getClasificacionDewey());
        if (!isNullOrEmpty(libro.getEstanteria())) libroActual.setEstanteria(libro.getEstanteria());
        if (!isNullOrEmpty(libro.getIdioma())) libroActual.setIdioma(libro.getIdioma());
        if (libro.getExistencias() != null) libroActual.setExistencias(libro.getExistencias());
        if (libro.getDisponibles() != null) libroActual.setDisponibles(libro.getDisponibles());
        if (libro.getEliminado() != null) libroActual.setEliminado(libro.getEliminado());

        validateLibro(libroActual, true);

        return libroDAO.updateOne(libroActual);
    }

    public List<Libro> updateManyLibros(List<Libro> librosParciales)  {
        if (librosParciales == null || librosParciales.isEmpty())
            throw new IllegalArgumentException("La lista de libros a actualizar no puede estar vacía.");

        List<Libro> librosActualizados = new ArrayList<>();

        for (Libro libroParcial : librosParciales) {
            if (isNullOrEmpty(libroParcial.getId()))
                throw new IllegalArgumentException("Cada libro a actualizar debe tener un ID.");

            Libro libroActual = libroDAO.findById(libroParcial.getId());
            if (libroActual == null)
                throw new EntityNotFoundException("No se encontró un libro con ID: " + libroParcial.getId());

            if (!isNullOrEmpty(libroParcial.getIsbn())) libroActual.setIsbn(libroParcial.getIsbn());
            if (!isNullOrEmpty(libroParcial.getTitulo())) libroActual.setTitulo(libroParcial.getTitulo());
            if (!isNullOrEmpty(libroParcial.getAutor())) libroActual.setAutor(libroParcial.getAutor());
            if (!isNullOrEmpty(libroParcial.getEditorial())) libroActual.setEditorial(libroParcial.getEditorial());
            if (libroParcial.getAnioEdicion() != null) libroActual.setAnioEdicion(libroParcial.getAnioEdicion());
            if (!isNullOrEmpty(libroParcial.getClasificacionDewey())) libroActual.setClasificacionDewey(libroParcial.getClasificacionDewey());
            if (!isNullOrEmpty(libroParcial.getEstanteria())) libroActual.setEstanteria(libroParcial.getEstanteria());
            if (!isNullOrEmpty(libroParcial.getIdioma())) libroActual.setIdioma(libroParcial.getIdioma());
            if (libroParcial.getExistencias() != null) libroActual.setExistencias(libroParcial.getExistencias());
            if (libroParcial.getDisponibles() != null) libroActual.setDisponibles(libroParcial.getDisponibles());
            if (libroParcial.getEliminado() != null) libroActual.setEliminado(libroParcial.getEliminado());

            validateLibro(libroActual, true);
            librosActualizados.add(libroActual);
        }

        return libroDAO.updateMany(librosActualizados);
    }


    public void deleteLibro(String id)  {
        if (isNullOrEmpty(id))
            throw new IllegalArgumentException("El ID del libro a eliminar no puede estar vacío.");

        libroDAO.deleteOne(id);
    }

    public void deleteManyLibros(List<String> ids)  {
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("La lista de IDs a eliminar no puede estar vacía.");

        libroDAO.deleteMany(ids);
    }

    public void totalDeleteOne(String id)  {
        if (isNullOrEmpty(id))
            throw new IllegalArgumentException("El ID del libro a eliminar físicamente no puede estar vacío.");

        libroDAO.totalDeleteOne(id);
    }

    public void totalDeleteMany(List<String> ids)  {
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("La lista de IDs a eliminar físicamente no puede estar vacía.");

        libroDAO.totalDeleteMany(ids);
    }
}
