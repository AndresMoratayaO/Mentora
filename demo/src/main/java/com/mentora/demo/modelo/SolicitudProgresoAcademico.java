package com.mentora.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudProgresoAcademico(

        @NotNull(message = "El estudiante es obligatorio")
        @Positive(message = "El identificador del estudiante debe ser positivo")
        Long estudianteId,

        @NotBlank(message = "La materia es obligatoria")
        String materia,

        @NotBlank(message = "La actividad es obligatoria")
        String actividad,

        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "0", message = "La nota no puede ser menor que 0")
        @DecimalMax(value = "100", message = "La nota no puede superar 100")
        BigDecimal nota,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        String observaciones

) {
}