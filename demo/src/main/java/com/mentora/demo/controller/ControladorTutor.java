package com.mentora.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.Materia;
import com.mentora.demo.modelo.Tutor;
import com.mentora.demo.repositorio.repositorioMateria;
import com.mentora.demo.repositorio.repositorioTutor;

@RestController
@RequestMapping("/tutores")
public class ControladorTutor {

    private final repositorioTutor repositorioTutor;
    private final repositorioMateria repositorioMateria;

    public ControladorTutor(
            repositorioTutor repositorioTutor,
            repositorioMateria repositorioMateria) {

        this.repositorioTutor = repositorioTutor;
        this.repositorioMateria = repositorioMateria;
    }

    @PostMapping
    public ResponseEntity<?> crearPerfilTutor(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String contrasenaHash,
            @RequestParam String experiencia,
            @RequestParam double precio,
            @RequestParam String descripcion) {

        String nombreLimpio = nombre.trim();
        String correoLimpio = correo.trim();
        String experienciaLimpia = experiencia.trim();
        String descripcionLimpia = descripcion.trim();

        if (nombreLimpio.isBlank()
                || correoLimpio.isBlank()
                || contrasenaHash.isBlank()
                || experienciaLimpia.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Los campos obligatorios deben completarse.");
        }

        if (precio < 0) {
            return ResponseEntity
                    .badRequest()
                    .body("El precio no puede ser negativo.");
        }

        Tutor tutor = new Tutor(
                nombreLimpio,
                correoLimpio,
                contrasenaHash,
                experienciaLimpia,
                precio,
                descripcionLimpia
        );

        return ResponseEntity.ok(
                repositorioTutor.save(tutor)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPerfilTutor(
            @PathVariable Long id,
            @RequestParam String experiencia,
            @RequestParam double precio,
            @RequestParam String descripcion) {

        Optional<Tutor> tutorEncontrado =
                repositorioTutor.findById(id);

        if (tutorEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (experiencia == null || experiencia.trim().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("La experiencia es obligatoria.");
        }

        if (precio < 0) {
            return ResponseEntity
                    .badRequest()
                    .body("El precio no puede ser negativo.");
        }

        Tutor tutor = tutorEncontrado.get();

        tutor.setExperiencia(experiencia.trim());
        tutor.setPrecio(precio);
        tutor.setDescripcion(
                descripcion == null
                        ? ""
                        : descripcion.trim()
        );

        return ResponseEntity.ok(
                repositorioTutor.save(tutor)
        );
    }

    @PostMapping("/{idTutor}/materias/{idMateria}")
    public ResponseEntity<?> agregarMateria(
            @PathVariable Long idTutor,
            @PathVariable Long idMateria) {

        Optional<Tutor> tutorEncontrado =
                repositorioTutor.findById(idTutor);

        Optional<Materia> materiaEncontrada =
                repositorioMateria.findById(idMateria);

        if (tutorEncontrado.isEmpty()
                || materiaEncontrada.isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();
        Materia materia = materiaEncontrada.get();

        tutor.agregarMateria(materia);

        return ResponseEntity.ok(
                repositorioTutor.save(tutor)
        );
    }

    @DeleteMapping("/{idTutor}/materias/{idMateria}")
    public ResponseEntity<?> eliminarMateria(
            @PathVariable Long idTutor,
            @PathVariable Long idMateria) {

        Optional<Tutor> tutorEncontrado =
                repositorioTutor.findById(idTutor);

        Optional<Materia> materiaEncontrada =
                repositorioMateria.findById(idMateria);

        if (tutorEncontrado.isEmpty()
                || materiaEncontrada.isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();

        tutor.eliminarMateria(
                materiaEncontrada.get()
        );

        return ResponseEntity.ok(
                repositorioTutor.save(tutor)
        );
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Tutor>> buscarPorMateria(
            @RequestParam String materia) {

        String materiaLimpia = materia.trim();

        List<Tutor> tutores =
                repositorioTutor
                        .findByMateriasNombreIgnoreCase(
                                materiaLimpia
                        );

        return ResponseEntity.ok(tutores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tutor> consultarTutor(
            @PathVariable Long id) {

        Optional<Tutor> tutor =
                repositorioTutor.findById(id);

        if (tutor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tutor.get());
    }

    @GetMapping
    public ResponseEntity<List<Tutor>> listarTutores() {

        return ResponseEntity.ok(
                repositorioTutor.findAll()
        );
    }
}