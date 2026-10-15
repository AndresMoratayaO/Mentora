package com.mentora.demo.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * Consulta los horarios del tutor.
     *
     * De forma predeterminada devuelve solamente
     * los horarios disponibles.
     */
    @GetMapping
    public ResponseEntity<?> listarDisponibilidades(
            @PathVariable Long tutorId,

            @RequestParam(
                    name = "soloDisponibles",
                    defaultValue = "true")
            boolean soloDisponibles) {

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

        List<Disponibilidad> disponibilidades;

        if (soloDisponibles) {
            disponibilidades = disponibilidadRepositorio
                    .findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
                            tutorId);
        } else {
            disponibilidades = disponibilidadRepositorio
                    .findByTutor_IdOrderByDiaAscHoraInicioAsc(tutorId);
        }

        List<DisponibilidadRespuesta> horarios = disponibilidades
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

        if (existeConflicto(
                tutorId,
                null,
                diaNormalizado,
                horaInicio,
                horaFin)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "El horario se cruza con otra disponibilidad activa."
                    );
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
     * Modifica el día y las horas de una disponibilidad.
     */
    @PutMapping("/{disponibilidadId}")
    public ResponseEntity<?> actualizarDisponibilidad(
            @PathVariable Long tutorId,
            @PathVariable Long disponibilidadId,

            @RequestParam("dia")
            String dia,

            @RequestParam("horaInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaInicio,

            @RequestParam("horaFin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaFin) {

        Disponibilidad disponibilidad = disponibilidadRepositorio
                .findByIdAndTutor_Id(disponibilidadId, tutorId)
                .orElse(null);

        if (disponibilidad == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El horario indicado no existe para este tutor.");
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

        /*
         * Solamente se verifica el conflicto si el horario
         * que se está editando se encuentra activo.
         */
        if (disponibilidad.isDisponible()
                && existeConflicto(
                        tutorId,
                        disponibilidadId,
                        diaNormalizado,
                        horaInicio,
                        horaFin)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "El horario se cruza con otra disponibilidad activa."
                    );
        }

        disponibilidad.setDia(diaNormalizado);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);

        Disponibilidad horarioActualizado =
                disponibilidadRepositorio.save(disponibilidad);

        return ResponseEntity.ok(
                convertirRespuesta(horarioActualizado));
    }

    /*
     * Activa o desactiva un horario.
     */
    @PatchMapping("/{disponibilidadId}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long tutorId,
            @PathVariable Long disponibilidadId,

            @RequestParam("disponible")
            boolean disponible) {

        Disponibilidad horario = disponibilidadRepositorio
                .findByIdAndTutor_Id(disponibilidadId, tutorId)
                .orElse(null);

        if (horario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El horario indicado no existe para este tutor.");
        }

        /*
         * Antes de activar un horario, se comprueba que
         * no se cruce con otro horario que ya esté activo.
         */
        if (disponible
                && existeConflicto(
                        tutorId,
                        disponibilidadId,
                        horario.getDia(),
                        horario.getHoraInicio(),
                        horario.getHoraFin())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "No se puede activar porque se cruza " +
                        "con otra disponibilidad activa."
                    );
        }

        horario.setDisponible(disponible);

        Disponibilidad horarioActualizado =
                disponibilidadRepositorio.save(horario);

        return ResponseEntity.ok(
                convertirRespuesta(horarioActualizado));
    }

    /*
     * Comprueba si un horario se cruza con otro horario
     * activo del mismo tutor y del mismo día.
     *
     * El identificador ignorado permite editar un horario
     * sin compararlo consigo mismo.
     */
    private boolean existeConflicto(
            Long tutorId,
            Long disponibilidadIdIgnorada,
            String dia,
            LocalTime horaInicio,
            LocalTime horaFin) {

        List<Disponibilidad> horariosActivos =
                disponibilidadRepositorio
                        .findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
                                tutorId);

        return horariosActivos
                .stream()
                .filter(horario ->
                        disponibilidadIdIgnorada == null
                        || !horario.getId().equals(
                                disponibilidadIdIgnorada))
                .filter(horario ->
                        horario.getDia().equalsIgnoreCase(dia))
                .anyMatch(horario ->
                        horaInicio.isBefore(horario.getHoraFin())
                        && horaFin.isAfter(
                                horario.getHoraInicio()));
    }

    /*
     * Convierte la entidad en una respuesta segura.
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
     * Valida y normaliza el día.
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
     * Información enviada como JSON.
     */
    public record DisponibilidadRespuesta(
            Long id,
            String dia,
            LocalTime horaInicio,
            LocalTime horaFin,
            boolean disponible) {
    }
}