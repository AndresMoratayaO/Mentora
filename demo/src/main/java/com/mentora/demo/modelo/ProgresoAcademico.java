package com.mentora.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProgresoAcademico(
        long id,
        long estudianteId,
        String materia,
        String actividad,
        BigDecimal nota,
        LocalDate fecha,
        String observaciones
) {
}