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
import com.mentora.demo.modelo.Tutor;
import com.mentora.demo.repositorio.DisponibilidadRepositorio;
import com.mentora.demo.repositorio.repositorioTutor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/tutores/{tutorId}/disponibilidades")
public class DisponibilidadController {

    private final DisponibilidadRepositorio disponibilidadRepositorio;
    private final repositorioTutor tutorRepositorio;

    public DisponibilidadController(
            DisponibilidadRepositorio disponibilidadRepositorio,
            repositorioTutor tutorRepositorio) {

        this.disponibilidadRepositorio = disponibilidadRepositorio;
        this.tutorRepositorio = tutorRepositorio;
    }

    private static final List<String> ORDEN_DIAS = List.of(
            "Lunes", "Martes", "Miércoles", "Jueves",
            "Viernes", "Sábado", "Domingo");

    /*
     * Solo el propio tutor (sesión iniciada como TUTOR con ese id)
     * puede crear o modificar sus horarios.
     * Devuelve null si está autorizado.
     */
    private ResponseEntity<String> verificarTutor(
            Long tutorId, HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null
                || sesion.getAttribute(ControladorSesion.ATTR_ID) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Debes iniciar sesión.");
        }

        boolean esTutor = "TUTOR".equals(
                sesion.getAttribute(ControladorSesion.ATTR_TIPO));
        boolean esElMismo = tutorId.equals(
                sesion.getAttribute(ControladorSesion.ATTR_ID));

        if (!esTutor || !esElMismo) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo el tutor dueño del horario puede modificarlo.");
        }

        return null;
    }

    private int posicionDia(Disponibilidad d) {
        int i = ORDEN_DIAS.indexOf(d.getDia());
        return i < 0 ? ORDEN_DIAS.size() : i;
    }

    /*
     * Consulta los horarios de un tutor.
     *
     * De forma predeterminada devuelve únicamente
     * los horarios que están disponibles.
     *
     * Para consultar también los horarios desactivados:
     *
     * ?soloDisponibles=false
     */
    @GetMapping
    public ResponseEntity<?> listarDisponibilidades(
            @PathVariable Long tutorId,

            @RequestParam(
                    name = "soloDisponibles",
                    defaultValue = "true")
            boolean soloDisponibles) {

        Tutor tutor = tutorRepositorio.findById(tutorId).orElse(null);

        if (tutor == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El tutor indicado no existe.");
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
                .sorted(java.util.Comparator
                        .comparingInt(this::posicionDia)
                        .thenComparing(Disponibilidad::getHoraInicio))
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
            LocalTime horaFin,

            HttpServletRequest request) {

        ResponseEntity<String> prohibido = verificarTutor(tutorId, request);

        if (prohibido != null) {
            return prohibido;
        }

        Tutor tutor = tutorRepositorio.findById(tutorId).orElse(null);

        if (tutor == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El tutor indicado no existe.");
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
     * Modifica el día, la hora inicial y la hora final
     * de un horario que pertenece al tutor.
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
            LocalTime horaFin,

            HttpServletRequest request) {

        ResponseEntity<String> prohibido = verificarTutor(tutorId, request);

        if (prohibido != null) {
            return prohibido;
        }

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
     *
     * Los horarios desactivados permanecen en la base
     * de datos, pero no se muestran como disponibles.
     */
    @PatchMapping("/{disponibilidadId}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long tutorId,
            @PathVariable Long disponibilidadId,

            @RequestParam("disponible")
            boolean disponible,

            HttpServletRequest request) {

        ResponseEntity<String> prohibido = verificarTutor(tutorId, request);

        if (prohibido != null) {
            return prohibido;
        }

        Disponibilidad horario = disponibilidadRepositorio
                .findByIdAndTutor_Id(disponibilidadId, tutorId)
                .orElse(null);

        if (horario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El horario indicado no existe para este tutor.");
        }

        horario.setDisponible(disponible);

        Disponibilidad horarioActualizado =
                disponibilidadRepositorio.save(horario);

        return ResponseEntity.ok(
                convertirRespuesta(horarioActualizado));
    }

    /*
     * Convierte la entidad Disponibilidad en una respuesta.
     *
     * De esta manera no se envían datos privados del tutor,
     * como el correo o la contraseña.
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
     * Valida y normaliza el nombre de un día.
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
     * Representa los datos que se devuelven como JSON.
     */
    public record DisponibilidadRespuesta(
            Long id,
            String dia,
            LocalTime horaInicio,
            LocalTime horaFin,
            boolean disponible) {
    }
}