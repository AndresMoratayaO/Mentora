package com.mentora.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
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

    public ControladorTutor(repositorioTutor repositorioTutor,
                            repositorioMateria repositorioMateria) {
        this.repositorioTutor = repositorioTutor;
        this.repositorioMateria = repositorioMateria;
    }

    @PostMapping
    public ResponseEntity<Tutor> crearPerfilTutor(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String contrasenaHash,
            @RequestParam String experiencia,
            @RequestParam double precio,
            @RequestParam String descripcion) {

        Tutor tutor = new Tutor(
                nombre,
                correo,
                contrasenaHash,
                experiencia,
                precio,
                descripcion
        );

        Tutor tutorGuardado = repositorioTutor.save(tutor);

        return ResponseEntity.ok(tutorGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tutor> actualizarPerfilTutor(
            @PathVariable Long id,
            @RequestParam String experiencia,
            @RequestParam double precio,
            @RequestParam String descripcion) {

        Optional<Tutor> tutorEncontrado = repositorioTutor.findById(id);

        if (tutorEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();

        tutor.setExperiencia(experiencia);
        tutor.setPrecio(precio);
        tutor.setDescripcion(descripcion);

        Tutor tutorActualizado = repositorioTutor.save(tutor);

        return ResponseEntity.ok(tutorActualizado);
    }

    @PostMapping("/{idTutor}/materias/{idMateria}")
    public ResponseEntity<Tutor> agregarMateria(
            @PathVariable Long idTutor,
            @PathVariable Long idMateria) {

        Optional<Tutor> tutorEncontrado =
                repositorioTutor.findById(idTutor);

        Optional<Materia> materiaEncontrada =
                repositorioMateria.findById(idMateria);

        if (tutorEncontrado.isEmpty() || materiaEncontrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();
        Materia materia = materiaEncontrada.get();

        tutor.agregarMateria(materia);

        Tutor tutorActualizado = repositorioTutor.save(tutor);

        return ResponseEntity.ok(tutorActualizado);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Tutor>> buscarPorMateria(
            @RequestParam String materia) {

        List<Tutor> tutores =
                repositorioTutor.findByMateriasNombreIgnoreCase(materia);

        return ResponseEntity.ok(tutores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tutor> consultarTutor(
            @PathVariable Long id) {

        Optional<Tutor> tutor = repositorioTutor.findById(id);

        if (tutor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tutor.get());
    }

    @GetMapping
    public ResponseEntity<List<Tutor>> listarTutores() {

        List<Tutor> tutores = repositorioTutor.findAll();

        return ResponseEntity.ok(tutores);
    }

    @GetMapping("/comparar")
    public ResponseEntity<List<Tutor>> compararTutores(
            @RequestParam Long idTutor1,
            @RequestParam Long idTutor2) {

        Optional<Tutor> tutor1 =
                repositorioTutor.findById(idTutor1);

        Optional<Tutor> tutor2 =
                repositorioTutor.findById(idTutor2);

        if (tutor1.isEmpty() || tutor2.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                List.of(tutor1.get(), tutor2.get())
        );
    }
}