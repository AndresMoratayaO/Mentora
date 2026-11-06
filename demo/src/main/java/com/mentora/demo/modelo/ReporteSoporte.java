
package com.mentora.demo.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "reportes_soporte")
public class ReporteSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 254)
    private String correo;

    @Column(nullable = false, length = 30)
    private String categoria;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(
        name = "fecha_creacion",
        nullable = false,
        updatable = false
    )
    private LocalDateTime fechaCreacion;

    public ReporteSoporte() {
    }

    @PrePersist
    public void antesDeGuardar() {
        if (estado == null || estado.isBlank()) {
            estado = "PENDIENTE";
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
        LocalDateTime fechaCreacion
    ) {
        this.fechaCreacion = fechaCreacion;
    }
}
