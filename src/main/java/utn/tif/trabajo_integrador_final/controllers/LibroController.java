package utn.tif.trabajo_integrador_final.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.tif.trabajo_integrador_final.models.Libro;
import utn.tif.trabajo_integrador_final.Services.LibroService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    @Autowired
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @PostMapping
    public ResponseEntity<?> createLibro(@RequestBody Libro libro) {
        try {
            Libro created = libroService.createLibro(libro);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el libro: " + e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createManyLibros(@RequestBody List<Libro> libros) {
        try {
            List<Libro> created = libroService.bulkCreateLibros(libros);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear los libros: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllLibros() {
        try {
            List<Libro> found = libroService.getAll();
            if (found.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(found);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar libros: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        try {
            Optional<Libro> found = libroService.findById(id);
            if (found.isPresent()) {
                return ResponseEntity.ok(found.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Libro no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar libro: " + e.getMessage());
        }
    }

    @GetMapping("/many")
    public ResponseEntity<List<Libro>> findMany(@RequestBody List<String> ids) {
        try {
            List<Libro> libros = libroService.findManyById(ids);

            return ResponseEntity.ok(libros);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLibro(@PathVariable String id, @RequestBody Libro libro) {
        try {
            libro.setId(id);
            Libro updated = libroService.updateLibro(libro);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el libro: " + e.getMessage());
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> updateManyLibros(@RequestBody List<Libro> libros) {
        try {
            List<Libro> updated = libroService.updateManyLibros(libros);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar varios libros: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLibro(@PathVariable String id) {
        try {
            libroService.deleteLibro(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar libro: " + e.getMessage());
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteMany(@RequestBody List<String> ids) {
        try {
            libroService.deleteManyLibros(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar varios libros: " + e.getMessage());
        }
    }

    @DeleteMapping("/total/{id}")
    public ResponseEntity<?> totalDeleteOne(@PathVariable String id) {
        try {
            libroService.totalDeleteOne(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar físicamente el libro: " + e.getMessage());
        }
    }

    @DeleteMapping("/total")
    public ResponseEntity<?> totalDeleteMany(@RequestBody List<String> ids) {
        try {
            libroService.totalDeleteMany(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar físicamente varios libros: " + e.getMessage());
        }
    }
}
