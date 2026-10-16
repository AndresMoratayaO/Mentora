package com.mentora.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.dto.TutorDTO;
import com.mentora.demo.modelo.Materia;
import com.mentora.demo.modelo.Tutor;
import com.mentora.demo.repositorio.repositorioMateria;
import com.mentora.demo.repositorio.repositorioTutor;

/**
 * Consulta y administración de perfiles de tutor.
 * La creación del tutor se hace desde /registro.
 * Todas las respuestas usan TutorDTO: nunca salen el correo ni el hash.
 */
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

    @PutMapping("/{id}")
    public ResponseEntity<TutorDTO> actualizarPerfilTutor(
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

        return ResponseEntity.ok(TutorDTO.desde(tutorActualizado));
    }

    @PostMapping("/{idTutor}/materias/{idMateria}")
    public ResponseEntity<TutorDTO> agregarMateria(
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

        return ResponseEntity.ok(TutorDTO.desde(tutorActualizado));
    }

    /**
     * Busca por materia o por nombre del tutor.
     * Sin texto devuelve todos los tutores.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<TutorDTO>> buscar(
            @RequestParam(value = "q", required = false,
                          defaultValue = "") String q) {

        String texto = q.trim();

        List<Tutor> tutores = texto.isEmpty()
                ? repositorioTutor.findAll(Sort.by("nombre"))
                : repositorioTutor.buscar(texto);

        return ResponseEntity.ok(
                tutores.stream().map(TutorDTO::desde).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorDTO> consultarTutor(
            @PathVariable Long id) {

        Optional<Tutor> tutor = repositorioTutor.findById(id);

        if (tutor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(TutorDTO.desde(tutor.get()));
    }

    @GetMapping
    public ResponseEntity<List<TutorDTO>> listarTutores() {

        List<Tutor> tutores = repositorioTutor.findAll(Sort.by("nombre"));

        return ResponseEntity.ok(
                tutores.stream().map(TutorDTO::desde).toList());
    }

    @GetMapping("/comparar")
    public ResponseEntity<List<TutorDTO>> compararTutores(
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
                List.of(TutorDTO.desde(tutor1.get()),
                        TutorDTO.desde(tutor2.get()))
        );
    }
}
