package com.mentora.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    /**
     * Solo el propio tutor (sesión iniciada como TUTOR con ese id)
     * puede modificar su perfil. Devuelve null si está autorizado.
     */
    private ResponseEntity<String> verificarDueno(
            Long idTutor, HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null
                || sesion.getAttribute(ControladorSesion.ATTR_ID) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Debes iniciar sesión.");
        }

        boolean esTutor = "TUTOR".equals(
                sesion.getAttribute(ControladorSesion.ATTR_TIPO));
        boolean esElMismo = idTutor.equals(
                sesion.getAttribute(ControladorSesion.ATTR_ID));

        if (!esTutor || !esElMismo) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo puedes modificar tu propio perfil.");
        }

        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPerfilTutor(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String experiencia,
            @RequestParam double precio,
            @RequestParam(required = false, defaultValue = "")
                String descripcion,
            HttpServletRequest request) {

        ResponseEntity<String> prohibido = verificarDueno(id, request);

        if (prohibido != null) {
            return prohibido;
        }

        nombre = nombre.trim();
        experiencia = experiencia.trim();
        descripcion = descripcion.trim();

        if (nombre.isBlank() || nombre.length() > 100) {
            return ResponseEntity.badRequest()
                    .body("El nombre es obligatorio y admite hasta 100 caracteres.");
        }

        if (experiencia.isBlank() || experiencia.length() > 255) {
            return ResponseEntity.badRequest()
                    .body("La experiencia es obligatoria: máximo 255 caracteres.");
        }

        if (!Double.isFinite(precio) || precio < 0) {
            return ResponseEntity.badRequest()
                    .body("Ingresa un precio válido, mayor o igual a cero.");
        }

        if (descripcion.length() > 500) {
            return ResponseEntity.badRequest()
                    .body("La descripción admite hasta 500 caracteres.");
        }

        Optional<Tutor> tutorEncontrado = repositorioTutor.findById(id);

        if (tutorEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();

        tutor.setNombre(nombre);
        tutor.setExperiencia(experiencia);
        tutor.setPrecio(precio);
        tutor.setDescripcion(descripcion);

        Tutor tutorActualizado = repositorioTutor.save(tutor);

        // El saludo del menú lee el nombre de la sesión: se mantiene al día.
        request.getSession(false)
                .setAttribute(ControladorSesion.ATTR_NOMBRE, nombre);

        return ResponseEntity.ok(TutorDTO.desde(tutorActualizado));
    }

    @PostMapping("/{idTutor}/materias/{idMateria}")
    public ResponseEntity<?> agregarMateria(
            @PathVariable Long idTutor,
            @PathVariable Long idMateria,
            HttpServletRequest request) {

        ResponseEntity<String> prohibido = verificarDueno(idTutor, request);

        if (prohibido != null) {
            return prohibido;
        }

        Optional<Tutor> tutorEncontrado =
                repositorioTutor.findById(idTutor);

        Optional<Materia> materiaEncontrada =
                repositorioMateria.findById(idMateria);

        if (tutorEncontrado.isEmpty() || materiaEncontrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();

        tutor.agregarMateria(materiaEncontrada.get());

        Tutor tutorActualizado = repositorioTutor.save(tutor);

        return ResponseEntity.ok(TutorDTO.desde(tutorActualizado));
    }

    @DeleteMapping("/{idTutor}/materias/{idMateria}")
    public ResponseEntity<?> quitarMateria(
            @PathVariable Long idTutor,
            @PathVariable Long idMateria,
            HttpServletRequest request) {

        ResponseEntity<String> prohibido = verificarDueno(idTutor, request);

        if (prohibido != null) {
            return prohibido;
        }

        Optional<Tutor> tutorEncontrado =
                repositorioTutor.findById(idTutor);

        if (tutorEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorEncontrado.get();

        if (tutor.getMaterias().size() <= 1) {
            return ResponseEntity.badRequest()
                    .body("Debes enseñar al menos una materia.");
        }

        tutor.getMaterias()
                .removeIf(m -> idMateria.equals(m.getIdMateria()));

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
