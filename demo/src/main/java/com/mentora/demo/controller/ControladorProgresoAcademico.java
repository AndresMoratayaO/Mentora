package com.mentora.demo.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mentora.demo.modelo.ProgresoAcademico;
import com.mentora.demo.modelo.SolicitudProgresoAcademico;
import com.mentora.demo.repositorio.RepositorioProgresoAcademico;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/progresos")
public class ControladorProgresoAcademico {

    private static final MediaType TEXTO =
            new MediaType("text", "plain", StandardCharsets.UTF_8);

    private final RepositorioProgresoAcademico repositorio;

    public ControladorProgresoAcademico(RepositorioProgresoAcademico repositorio) {
        this.repositorio = repositorio;
    }

    /** Registra un progreso para el estudiante que tiene la sesión iniciada. */
    @PostMapping
    public ResponseEntity<ProgresoAcademico> registrar(
            @Valid @RequestBody SolicitudProgresoAcademico solicitud,
            HttpServletRequest request) {

        Long idEstudiante = exigirEstudiante(request);

        ProgresoAcademico progreso = repositorio.save(new ProgresoAcademico(
                idEstudiante,
                solicitud.materia().trim(),
                solicitud.actividad().trim(),
                solicitud.nota(),
                solicitud.fecha(),
                solicitud.observaciones()));

        return ResponseEntity
                .created(URI.create("/progresos/" + progreso.getId()))
                .body(progreso);
    }

    /** Lista el progreso del estudiante con sesión iniciada (más reciente primero). */
    @GetMapping("/mio")
    public List<ProgresoAcademico> consultarMio(HttpServletRequest request) {

        Long idEstudiante = exigirEstudiante(request);

        return repositorio.findByEstudianteIdOrderByFechaDescIdDesc(idEstudiante);
    }

    /** Un registro puntual; solo si pertenece al estudiante de la sesión. */
    @GetMapping("/{id}")
    public ProgresoAcademico consultarPorId(
            @PathVariable("id") long id,
            HttpServletRequest request) {

        Long idEstudiante = exigirEstudiante(request);

        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El identificador debe ser positivo");
        }

        return repositorio.findById(id)
                .filter(p -> p.getEstudianteId().equals(idEstudiante))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un progreso con ese identificador"));
    }

    /** Devuelve el id del estudiante de la sesión o corta con 401/403. */
    private Long exigirEstudiante(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute(ControladorSesion.ATTR_ID) == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }

        if (!"ESTUDIANTE".equals(sesion.getAttribute(ControladorSesion.ATTR_TIPO))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo los estudiantes tienen progreso académico");
        }

        return (Long) sesion.getAttribute(ControladorSesion.ATTR_ID);
    }

    // ------------------------------------------------------------------
    // Errores en texto plano: mentora.js (leerMensaje) solo muestra el
    // mensaje cuando la respuesta es text/plain.
    // ------------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> alFallarValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getDefaultMessage())
                .orElse("Datos inválidos");
        return texto(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> alLeerMal(HttpMessageNotReadableException e) {
        return texto(HttpStatus.BAD_REQUEST,
                "Los datos enviados no tienen el formato correcto (revisa la nota y la fecha)");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> alLanzarEstado(ResponseStatusException e) {
        HttpStatusCode codigo = e.getStatusCode();
        String razon = e.getReason() != null ? e.getReason() : "Error";
        return ResponseEntity.status(codigo).contentType(TEXTO).body(razon);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> alFallarBaseDeDatos(DataAccessException e) {
        e.printStackTrace();
        return texto(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error en la base de datos. ¿Ya creaste la tabla progreso_academico?");
    }

    private ResponseEntity<String> texto(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).contentType(TEXTO).body(mensaje);
    }
}