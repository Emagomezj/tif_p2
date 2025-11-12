package utn.tif.trabajo_integrador_final.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.tif.trabajo_integrador_final.Services.PrestamoService;
import utn.tif.trabajo_integrador_final.models.Prestamo;

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
            return ResponseEntity.ok(prestamoService.createPrestamo(prestamo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulkCreate(@RequestBody List<Prestamo> prestamos) {
        try {
            return ResponseEntity.ok(prestamoService.bulkCreatePrestamos(prestamos));
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
        try {
            prestamoService.deletePrestamo(id);
            return ResponseEntity.ok("Préstamo eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteManyPrestamos(@RequestBody List<String> ids) {
        try {
            prestamoService.deleteManyPrestamos(ids);
            return ResponseEntity.ok("Préstamos eliminados correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(prestamoService.getByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<?> getByLibro(@PathVariable String libroId) {
        try {
            return ResponseEntity.ok(prestamoService.getByLibro(libroId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getByEstado(@PathVariable String estado) {
        try {
            return ResponseEntity.ok(prestamoService.getByEstado(estado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/actualizar-estado")
    public ResponseEntity<?> updateEstado() {
        try {
            return ResponseEntity.ok(prestamoService.updateEstado());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/plazo")
    public ResponseEntity<?> modifyPlazo(@PathVariable String id, @RequestParam("newPlazo") String newPlazo) {
        try {
            LocalDate fecha = LocalDate.parse(newPlazo);
            prestamoService.modifyPlazo(Integer.parseInt(id), fecha);
            return ResponseEntity.ok("Plazo modificado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/vencidos")
    public ResponseEntity<?> getVencidos() {
        try {
            return ResponseEntity.ok(prestamoService.obtenerPrestamosVencidos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
