package com.mentora.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.Estudiante;
import com.mentora.demo.modelo.Opinion;
import com.mentora.demo.modelo.Tutor;
import com.mentora.demo.repositorio.RepositorioOpinion;
import com.mentora.demo.repositorio.repositorioEstudiante;
import com.mentora.demo.repositorio.repositorioTutor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Opiniones de estudiantes sobre tutores.
 * Quién opina sale de la sesión (no de un parámetro), así nadie
 * puede opinar a nombre de otro estudiante.
 */
@RestController
@RequestMapping("/opiniones")
public class ControladorOpinion {

    private final RepositorioOpinion repositorioOpinion;
    private final repositorioTutor repositorioTutor;
    private final repositorioEstudiante repositorioEstudiante;

    public ControladorOpinion(
            RepositorioOpinion repositorioOpinion,
            repositorioTutor repositorioTutor,
            repositorioEstudiante repositorioEstudiante) {

        this.repositorioOpinion = repositorioOpinion;
        this.repositorioTutor = repositorioTutor;
        this.repositorioEstudiante = repositorioEstudiante;
    }

    @PostMapping
    public ResponseEntity<?> registrarOpinion(
            @RequestParam Long idTutor,
            @RequestParam String comentario,
            @RequestParam int valoracion,
            HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute(ControladorSesion.ATTR_ID) == null) {
            return ResponseEntity.status(401)
                    .body("Debes iniciar sesión para opinar.");
        }

        if (!"ESTUDIANTE".equals(sesion.getAttribute(ControladorSesion.ATTR_TIPO))) {
            return ResponseEntity.status(403)
                    .body("Solo un estudiante puede opinar.");
        }

        Long idEstudiante = (Long) sesion.getAttribute(ControladorSesion.ATTR_ID);

        Optional<Estudiante> estudiante =
                repositorioEstudiante.findById(idEstudiante);

        Optional<Tutor> tutor = repositorioTutor.findById(idTutor);

        if (estudiante.isEmpty() || tutor.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("El tutor o el estudiante no existe.");
        }

        if (comentario == null || comentario.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("El comentario es obligatorio.");
        }

        if (comentario.trim().length() > 1000) {
            return ResponseEntity.badRequest()
                    .body("El comentario supera los 1000 caracteres.");
        }

        if (valoracion < 1 || valoracion > 5) {
            return ResponseEntity.badRequest()
                    .body("La valoración debe estar entre 1 y 5.");
        }

        Opinion opinion = new Opinion(
                estudiante.get(),
                tutor.get(),
                comentario,
                valoracion
        );

        Opinion guardada = repositorioOpinion.save(opinion);

        return ResponseEntity.status(201).body(convertirRespuesta(guardada));
    }

    @GetMapping("/tutor/{idTutor}")
    public ResponseEntity<?> consultarOpiniones(
            @PathVariable Long idTutor) {

        if (!repositorioTutor.existsById(idTutor)) {
            return ResponseEntity.status(404).body("El tutor no existe.");
        }

        List<Opinion> opiniones =
                repositorioOpinion
                        .findByTutor_IdOrderByIdOpinionDesc(idTutor);

        List<Map<String, Object>> resultado = opiniones.stream()
                .map(this::convertirRespuesta)
                .toList();

        double promedio = opiniones.stream()
                .mapToInt(Opinion::getValoracion)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok(Map.of(
                "idTutor", idTutor,
                "totalOpiniones", opiniones.size(),
                "promedio", Math.round(promedio * 10.0) / 10.0,
                "opiniones", resultado
        ));
    }

    private Map<String, Object> convertirRespuesta(Opinion opinion) {

        return Map.of(
                "idOpinion", opinion.getIdOpinion(),
                "idTutor", opinion.getTutor().getId(),
                "idEstudiante", opinion.getEstudiante().getId(),
                "nombreEstudiante", opinion.getEstudiante().getNombre(),
                "comentario", opinion.getComentario(),
                "valoracion", opinion.getValoracion()
        );
    }
}
