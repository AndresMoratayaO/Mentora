package com.mentora.demo.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.Disponibilidad;
import com.mentora.demo.modelo.Usuario;
import com.mentora.demo.repositorio.DisponibilidadRepositorio;
import com.mentora.demo.repositorio.repositoriousuario;

@RestController
@RequestMapping("/api/tutores/{tutorId}/disponibilidades")
public class DisponibilidadController {

    private final DisponibilidadRepositorio disponibilidadRepositorio;
    private final repositoriousuario usuarioRepositorio;

    public DisponibilidadController(
            DisponibilidadRepositorio disponibilidadRepositorio,
            repositoriousuario usuarioRepositorio) {

        this.disponibilidadRepositorio = disponibilidadRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /*
     * Devuelve los horarios disponibles del tutor.
     */
    @GetMapping
    public ResponseEntity<?> listarDisponibilidades(
            @PathVariable Long tutorId) {

        Usuario tutor = usuarioRepositorio.findById(tutorId).orElse(null);

        if (tutor == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El tutor indicado no existe.");
        }

        if (!"TUTOR".equalsIgnoreCase(tutor.getTipoUsuario())) {
            return ResponseEntity
                    .badRequest()
                    .body("El usuario indicado no es un tutor.");
        }

        List<DisponibilidadRespuesta> horarios =
                disponibilidadRepositorio
                        .findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
                                tutorId)
                        .stream()
                        .map(this::convertirRespuesta)
                        .toList();

        return ResponseEntity.ok(horarios);
    }

    /*
     * Registra un nuevo horario para el tutor.
     */
    @PostMapping
    public ResponseEntity<?> crearDisponibilidad(
            @PathVariable Long tutorId,

            @RequestParam("dia")
            String dia,

            @RequestParam("horaInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaInicio,

            @RequestParam("horaFin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaFin) {

        Usuario tutor = usuarioRepositorio.findById(tutorId).orElse(null);

        if (tutor == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El tutor indicado no existe.");
        }

        if (!"TUTOR".equalsIgnoreCase(tutor.getTipoUsuario())) {
            return ResponseEntity
                    .badRequest()
                    .body("El usuario indicado no es un tutor.");
        }

        String diaNormalizado = normalizarDia(dia);

        if (diaNormalizado == null) {
            return ResponseEntity
                    .badRequest()
                    .body("El día ingresado no es válido.");
        }

        if (!horaFin.isAfter(horaInicio)) {
            return ResponseEntity
                    .badRequest()
                    .body("La hora final debe ser posterior a la hora inicial.");
        }

        Disponibilidad disponibilidad = new Disponibilidad(
                tutor,
                diaNormalizado,
                horaInicio,
                horaFin);

        Disponibilidad horarioGuardado =
                disponibilidadRepositorio.save(disponibilidad);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirRespuesta(horarioGuardado));
    }

    /*
     * Convierte la entidad en una respuesta segura.
     * Así no se envían los datos privados del usuario.
     */
    private DisponibilidadRespuesta convertirRespuesta(
            Disponibilidad disponibilidad) {

        return new DisponibilidadRespuesta(
                disponibilidad.getId(),
                disponibilidad.getDia(),
                disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin(),
                disponibilidad.isDisponible());
    }

    /*
     * Comprueba y normaliza los nombres de los días.
     */
    private String normalizarDia(String dia) {

        if (dia == null) {
            return null;
        }

        String valor = dia.trim().toLowerCase(Locale.ROOT);

        return switch (valor) {
            case "lunes" -> "Lunes";
            case "martes" -> "Martes";
            case "miércoles", "miercoles" -> "Miércoles";
            case "jueves" -> "Jueves";
            case "viernes" -> "Viernes";
            case "sábado", "sabado" -> "Sábado";
            case "domingo" -> "Domingo";
            default -> null;
        };
    }

    /*
     * Define la información que se devolverá como JSON.
     */
    public record DisponibilidadRespuesta(
            Long id,
            String dia,
            LocalTime horaInicio,
            LocalTime horaFin,
            boolean disponible) {
    }
}