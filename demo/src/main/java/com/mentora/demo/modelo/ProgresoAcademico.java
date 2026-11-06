package com.mentora.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Registro de progreso académico de un estudiante.
 * Se guarda en la tabla "progreso_academico".
 */
@Entity
@Table(name = "progreso_academico")
public class ProgresoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(nullable = false, length = 100)
    private String materia;

    @Column(nullable = false, length = 200)
    private String actividad;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal nota;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 500)
    private String observaciones;

    protected ProgresoAcademico() {
    }

    public ProgresoAcademico(Long estudianteId, String materia, String actividad,
                             BigDecimal nota, LocalDate fecha, String observaciones) {
        this.estudianteId = estudianteId;
        this.materia = materia;
        this.actividad = actividad;
        this.nota = nota;
        this.fecha = fecha;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public String getMateria() {
        return materia;
    }

    public String getActividad() {
        return actividad;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }
}