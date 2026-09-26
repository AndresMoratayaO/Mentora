package com.mentora.demo.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutores")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Tutor extends Usuario {

    @Column(nullable = false, length = 255)
    private String experiencia;

    @Column(nullable = false)
    private double precio;

    @Column(length = 500)
    private String descripcion;

    @ManyToMany
    @JoinTable(
        name = "tutor_materia",
        joinColumns = @JoinColumn(name = "id_tutor"),
        inverseJoinColumns = @JoinColumn(name = "id_materia")
    )
    private List<Materia> materias = new ArrayList<>();

    protected Tutor() {
        super();
    }

    public Tutor(String nombre, String correo, String contrasenaHash,
                 String experiencia, double precio, String descripcion) {
        super(nombre, correo, contrasenaHash, "TUTOR");
        this.experiencia = experiencia;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Long getIdTutor() {
        return getId();
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }

    public void agregarMateria(Materia materia) {
        if (materia != null && !materias.contains(materia)) {
            materias.add(materia);
        }
    }

    public void eliminarMateria(Materia materia) {
        materias.remove(materia);
    }

    @Override
    public String toString() {
        return "Tutor{" +
                "idTutor=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                ", materias=" + materias +
                '}';
    }
}