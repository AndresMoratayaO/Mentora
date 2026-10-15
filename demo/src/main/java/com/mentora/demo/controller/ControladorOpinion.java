
package com.mentora.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mentora.demo.modelo.Opinion;
import com.mentora.demo.modelo.Tutor;
import com.mentora.demo.modelo.Usuario;
import com.mentora.demo.repositorio.repositorioOpinion;
import com.mentora.demo.repositorio.repositorioTutor;
import com.mentora.demo.repositorio.repositoriousuario;

@RestController
@RequestMapping("/opiniones")
public class ControladorOpinion {

    private final repositorioOpinion repositorioOpinion;
    private final repositorioTutor repositorioTutor;
    private final repositoriousuario repositorioUsuario;

    public ControladorOpinion(
            repositorioOpinion repositorioOpinion,
            repositorioTutor repositorioTutor,
            repositoriousuario repositorioUsuario) {

        this.repositorioOpinion = repositorioOpinion;
        this.repositorioTutor = repositorioTutor;
        this.repositorioUsuario = repositorioUsuario;
    }

    @PostMapping
    public ResponseEntity<?> registrarOpinion(
            @RequestParam Long idEstudiante,
            @RequestParam Long idTutor,
            @RequestParam String comentario,
            @RequestParam int valoracion) {

        Optional<Usuario> estudiante =
                repositorioUsuario.findById(idEstudiante);

        Optional<Tutor> tutor =
                repositorioTutor.findById(idTutor);

        if (estudiante.isEmpty() || tutor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!"ESTUDIANTE".equalsIgnoreCase(
                estudiante.get().getTipoUsuario())) {

            return ResponseEntity.badRequest()
                    .body("Solo un estudiante puede opinar.");
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

        return ResponseEntity.ok(convertirRespuesta(guardada));
    }

    @GetMapping("/tutor/{idTutor}")
    public ResponseEntity<?> consultarOpiniones(
            @PathVariable Long idTutor) {

        if (!repositorioTutor.existsById(idTutor)) {
            return ResponseEntity.notFound().build();
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
                "idTutor", opinion.getTutor().getIdTutor(),
                "idEstudiante", opinion.getEstudiante().getId(),
                "nombreEstudiante", opinion.getEstudiante().getNombre(),
                "comentario", opinion.getComentario(),
                "valoracion", opinion.getValoracion()
        );
    }
}
