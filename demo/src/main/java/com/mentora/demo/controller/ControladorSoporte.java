
package com.mentora.demo.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.ReporteSoporte;
import com.mentora.demo.repositorio.RepositorioSoporte;

@RestController
@RequestMapping("/api/soporte")
public class ControladorSoporte {

    private final RepositorioSoporte repositorio;

    private static final Set<String> CATEGORIAS = Set.of(
        "INICIO_SESION",
        "REGISTRO",
        "TUTORES",
        "HORARIOS",
        "OPINIONES",
        "OTRO"
    );

    public ControladorSoporte(RepositorioSoporte repositorio) {
        this.repositorio = repositorio;
    }

    // Datos que recibimos del formulario
    public record SolicitudSoporte(
        String nombre,
        String correo,
        String categoria,
        String asunto,
        String descripcion
    ) {}

    @PostMapping
    public ResponseEntity<?> crearReporte(
            @RequestBody SolicitudSoporte solicitud) {

        if (solicitud == null) {
            return error("Debes enviar los datos del reporte.");
        }

        String nombre = limpiar(solicitud.nombre());
        String correo = limpiar(solicitud.correo());
        String categoria = limpiar(solicitud.categoria());
        String asunto = limpiar(solicitud.asunto());
        String descripcion = limpiar(solicitud.descripcion());

        if (nombre.isEmpty() || correo.isEmpty()
                || categoria.isEmpty() || asunto.isEmpty()
                || descripcion.isEmpty()) {
            return error("Todos los campos son obligatorios.");
        }

        if (nombre.length() > 100 || correo.length() > 254
                || asunto.length() > 150
                || descripcion.length() > 2000) {
            return error("Uno de los campos supera el límite permitido.");
        }

        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return error("Ingresa un correo electrónico válido.");
        }

        if (!CATEGORIAS.contains(categoria)) {
            return error("Selecciona una categoría válida.");
        }

        ReporteSoporte reporte = new ReporteSoporte();

        reporte.setNombre(nombre);
        reporte.setCorreo(correo);
        reporte.setCategoria(categoria);
        reporte.setAsunto(asunto);
        reporte.setDescripcion(descripcion);

        // El estado y la fecha se establecen en el servidor.
        reporte.setEstado("PENDIENTE");

        ReporteSoporte guardado = repositorio.save(reporte);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "mensaje", "Reporte registrado correctamente.",
                "id", guardado.getId(),
                "estado", guardado.getEstado()
            )
        );
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private ResponseEntity<Map<String, String>> error(String mensaje) {
        return ResponseEntity.badRequest().body(
            Map.of("error", mensaje)
        );
    }
}
