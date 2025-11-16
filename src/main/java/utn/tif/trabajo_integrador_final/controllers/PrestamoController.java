package utn.tif.trabajo_integrador_final.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.tif.trabajo_integrador_final.Services.PrestamoService;
import utn.tif.trabajo_integrador_final.entities.Prestamo;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @Autowired
    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<?> createPrestamo(@RequestBody Prestamo prestamo) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.createPrestamo(prestamo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulkCreate(@RequestBody List<Prestamo> prestamos) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.bulkCreatePrestamos(prestamos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(prestamoService.getAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(prestamoService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/many")
    public ResponseEntity<?> getManyById(@RequestBody List<String> ids) {
        try {
            return ResponseEntity.ok(prestamoService.getManyById(ids));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrestamo(@PathVariable String id, @RequestBody Prestamo prestamo) {
        try {
            return ResponseEntity.ok(prestamoService.updatePrestamo(id, prestamo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> updateManyPrestamos(@RequestBody List<Prestamo> prestamos) {
        try {
            return ResponseEntity.ok(prestamoService.updateManyPrestamos(prestamos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrestamo(@PathVariable String id) {
        prestamoService.deletePrestamo(id);
        return ResponseEntity.ok("Préstamo eliminado correctamente");
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteManyPrestamos(@RequestBody List<String> ids) {
        prestamoService.deleteManyPrestamos(ids);
        return ResponseEntity.ok("Préstamos eliminados correctamente");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(prestamoService.getByUser(userId));
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<?> getByLibro(@PathVariable String libroId) {
        return ResponseEntity.ok(prestamoService.getByLibro(libroId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(prestamoService.getByEstado(estado));
    }

    @PutMapping("/actualizar-estado")
    public ResponseEntity<?> updateEstado() {
        return ResponseEntity.ok(prestamoService.updateEstado());
    }

    @PutMapping("/{id}/plazo")
    public ResponseEntity<?> modifyPlazo(@PathVariable String id, @RequestParam("newPlazo") String newPlazo) {
        LocalDate fecha = LocalDate.parse(newPlazo);
        prestamoService.modifyPlazo(Integer.parseInt(id), fecha);
        return ResponseEntity.ok("Plazo modificado correctamente");
    }

    @GetMapping("/vencidos")
    public ResponseEntity<?> getVencidos() {
        return ResponseEntity.ok(prestamoService.obtenerPrestamosVencidos());
    }
}
