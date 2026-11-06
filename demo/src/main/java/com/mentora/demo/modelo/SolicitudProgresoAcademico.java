package com.mentora.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos que envía el estudiante. El id del estudiante NO viaja aquí:
 * se toma de la sesión para que nadie pueda registrar progreso ajeno.
 */
public record SolicitudProgresoAcademico(

        @NotBlank(message = "La materia es obligatoria")
        @Size(max = 100, message = "La materia no puede superar 100 caracteres")
        String materia,

        @NotBlank(message = "La actividad es obligatoria")
        @Size(max = 200, message = "La actividad no puede superar 200 caracteres")
        String actividad,

        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "0", message = "La nota no puede ser menor que 0")
        @DecimalMax(value = "100", message = "La nota no puede superar 100")
        BigDecimal nota,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
        String observaciones

) {
}