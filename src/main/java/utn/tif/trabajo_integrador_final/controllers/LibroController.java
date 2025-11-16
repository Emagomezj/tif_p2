package utn.tif.trabajo_integrador_final.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.tif.trabajo_integrador_final.entities.Libro;
import utn.tif.trabajo_integrador_final.Services.LibroService;
import utn.tif.trabajo_integrador_final.exceptions.EntityNotFoundException;

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
    public ResponseEntity<Libro> createLibro(@RequestBody Libro libro) {
        Libro created = libroService.createLibro(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Libro>> createManyLibros(@RequestBody List<Libro> libros) {
        List<Libro> created = libroService.bulkCreateLibros(libros);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Libro>> getAllLibros() {
        List<Libro> found = libroService.getAll();
        if (found.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(found);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> findById(@PathVariable String id) {
        Optional<Libro> found = libroService.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con ID: " + id));
    }

    @GetMapping("/many")
    public ResponseEntity<List<Libro>> findMany(@RequestBody List<String> ids) {
        List<Libro> libros = libroService.findManyById(ids);
        return ResponseEntity.ok(libros);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> updateLibro(@PathVariable String id, @RequestBody Libro libro) {
        libro.setId(id);
        Libro updated = libroService.updateLibro(libro);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<Libro>> updateManyLibros(@RequestBody List<Libro> libros) {
        List<Libro> updated = libroService.updateManyLibros(libros);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable String id) {
        libroService.deleteLibro(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteMany(@RequestBody List<String> ids) {
        libroService.deleteManyLibros(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/total/{id}")
    public ResponseEntity<Void> totalDeleteOne(@PathVariable String id) {
        libroService.totalDeleteOne(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/total")
    public ResponseEntity<Void> totalDeleteMany(@RequestBody List<String> ids) {
        libroService.totalDeleteMany(ids);
        return ResponseEntity.noContent().build();
    }
}