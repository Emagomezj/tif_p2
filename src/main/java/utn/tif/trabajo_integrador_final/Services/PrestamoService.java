package utn.tif.trabajo_integrador_final.Services;

import utn.tif.trabajo_integrador_final.constants.EstadoPrestamo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utn.tif.trabajo_integrador_final.DAOS.LibroDAO;
import utn.tif.trabajo_integrador_final.DAOS.PrestamoDAO;
import utn.tif.trabajo_integrador_final.DAOS.UserDAO;
import utn.tif.trabajo_integrador_final.exceptions.EntityNotFoundException;
import utn.tif.trabajo_integrador_final.entities.Libro;
import utn.tif.trabajo_integrador_final.entities.Prestamo;
import utn.tif.trabajo_integrador_final.entities.Usuario;
import utn.tif.trabajo_integrador_final.exceptions.StockException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamoService {

    private final PrestamoDAO prestamoDAO;
    private final UserDAO userDAO;
    private final LibroDAO  libroDAO;

    @Autowired
    public PrestamoService(PrestamoDAO prestamoDAO,  UserDAO userDAO, LibroDAO libroDAO) {
        this.prestamoDAO = prestamoDAO;
        this.userDAO = userDAO;
        this.libroDAO = libroDAO;
    }


    public Prestamo createPrestamo(Prestamo prestamo)  {
        if (prestamo.getUserId() == null || prestamo.getLibroId() == null) {
            throw new IllegalArgumentException("El ID de usuario y libro son obligatorios");
        }
        Usuario user = userDAO.findById(prestamo.getUserId());
        Libro libro = libroDAO.findById(prestamo.getLibroId());
        if (user == null || libro == null) {
            throw new EntityNotFoundException("El usuario o el libro no existe");
        }
        if (libro.getDisponibles() > 0) {
            libro.setExistencias(libro.getExistencias() - 1);
            libroDAO.updateOne(libro);
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setFechaPlazo(LocalDate.now().plusDays(30));
            prestamo.setFechaDevolucion(null);
            prestamo.setEstado(EstadoPrestamo.ACTIVO.getValue());
            return prestamoDAO.save(prestamo);
        } else {
            throw new StockException("No hay existencias disponibles");
        }
    }

    public List<Prestamo> bulkCreatePrestamos(List<Prestamo> prestamos)  {
        if (prestamos == null || prestamos.isEmpty()) {
            throw new IllegalArgumentException("La lista de préstamos no puede estar vacía");
        }
        ArrayList<Libro> libros = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getUserId() == null || prestamo.getLibroId() == null) {
                throw new IllegalArgumentException("El ID de usuario y libro son obligatorios");
            }
            Usuario user = userDAO.findById(prestamo.getUserId());
            Libro libro = libroDAO.findById(prestamo.getLibroId());
            if (user == null || libro == null) {
                throw new EntityNotFoundException("El usuario o el libro no existe");
            }
            if (libro.getDisponibles() > 0) {
                libro.setExistencias(libro.getExistencias() - 1);
                libros.add(libro);
                prestamo.setFechaPrestamo(LocalDate.now());
                prestamo.setFechaPlazo(LocalDate.now().plusDays(30));
                prestamo.setFechaDevolucion(null);
                prestamo.setEstado(EstadoPrestamo.ACTIVO.getValue());
            } else {
                throw new StockException("No hay existencias disponibles del libro: " + libro);
            }
        }
        libroDAO.updateMany(libros);
        return prestamoDAO.bulkCreate(prestamos);
    }

    public Prestamo getById(String id)  {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        return prestamoDAO.findById(id);
    }

    public List<Prestamo> getAll()  {
        return prestamoDAO.findAll();
    }

    public List<Prestamo> getManyById(List<String> ids)  {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("La lista de IDs no puede estar vacía");
        }
        return prestamoDAO.findMany(ids);
    }

    public Prestamo updatePrestamo(String id, Prestamo prestamo)  {
        if (id == null) {
            throw new IllegalArgumentException("El ID del préstamo es obligatorio para actualizar");
        }
        Prestamo p = prestamoDAO.findById(id);
        Libro libro = libroDAO.findById(prestamo.getLibroId());

        if (p == null) {
            throw new EntityNotFoundException("Prestamo inexistente");
        }
        if (libro == null) {
            throw new EntityNotFoundException("Libro inexistente");
        }
        if(prestamo.getFechaDevolucion() != null) {
            p.setFechaDevolucion(prestamo.getFechaDevolucion());
            p.setEstado(EstadoPrestamo.DEVUELTO.getValue());
            libro.setDisponibles(libro.getDisponibles() + 1);
        }
        libroDAO.updateOne(libro);
        return prestamoDAO.updateOne(p);
    }

    public List<Prestamo> updateManyPrestamos(List<Prestamo> prestamos)  {
        if (prestamos == null || prestamos.isEmpty()) {
            throw new IllegalArgumentException("La lista de préstamos no puede estar vacía");
        }
        ArrayList<Prestamo> ps = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getId() == null) {
                throw new IllegalArgumentException("Se debe proporcionar el id del prestamo");
            }
            Prestamo p = prestamoDAO.findById(String.valueOf(prestamo.getId()));
            if (p == null) {
                throw new EntityNotFoundException("Prestamo inexistente");
            }
            if(prestamo.getFechaDevolucion() != null) {
                p.setFechaDevolucion(prestamo.getFechaDevolucion());
                p.setEstado(EstadoPrestamo.DEVUELTO.getValue());
                ps.add(p);
            }

        }

        if (ps.size() > 0) {
            return prestamoDAO.updateMany(ps);
        } else {
            return ps;
        }
    }

    public void deletePrestamo(String id)  {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El ID del préstamo es obligatorio");
        }
        Prestamo p = prestamoDAO.findById(id);
        if (p == null) {
            throw new EntityNotFoundException("Prestamo inexistente");
        }
        prestamoDAO.deleteOne(id);
    }

    public void deleteManyPrestamos(List<String> ids)  {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("La lista de IDs no puede estar vacía");
        }
        prestamoDAO.deleteMany(ids);
    }

    public List<Prestamo> getByUser(String userId)  {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio");
        }
        return prestamoDAO.findByUserId(userId);
    }

    public List<Prestamo> getByLibro(String libroId)  {
        if (libroId == null || libroId.isEmpty()) {
            throw new IllegalArgumentException("El ID de libro es obligatorio");
        }
        return prestamoDAO.findByLibroId(libroId);
    }

    public List<Prestamo> getByEstado(String estado)  {
        if (estado == null || estado.isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        return prestamoDAO.findByEstado(estado);
    }

    public List<Prestamo> obtenerPrestamosVencidos()  {
        return prestamoDAO.findPrestamosVencidos_update();
    }

    public List<Prestamo> updateEstado()  {
        List<Prestamo> vencidos = prestamoDAO.findPrestamosVencidos_update();
        ArrayList<Prestamo> ps = new ArrayList<>();
        for (Prestamo prestamo : vencidos) {
            prestamo.setEstado(EstadoPrestamo.VENCIDO.getValue());
            ps.add(prestamo);
        }
        return prestamoDAO.updateMany(ps);
    }

    public void modifyPlazo(int id, LocalDate newPlazo)  {
        Prestamo p = prestamoDAO.findById(String.valueOf(id));
        if (p == null) {
            throw new EntityNotFoundException("Prestamo inexistente");
        }
        p.setFechaPlazo(newPlazo);
        prestamoDAO.updateOne(p);
    }
}
