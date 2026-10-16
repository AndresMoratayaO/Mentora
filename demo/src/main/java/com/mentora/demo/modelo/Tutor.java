package com.mentora.demo.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Cuenta de tutor. Se guarda en su propia tabla: "tutores".
 * Ya no hereda de Usuario: estudiantes y tutores son tablas independientes.
 */
@Entity
@Table(name = "tutores")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 254)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    @Column(nullable = false, length = 255)
    private String experiencia;

    @Column(nullable = false)
    private double precio;

    @Column(length = 500)
    private String descripcion;

    // EAGER: la lista de materias es pequeña y siempre se muestra junto al tutor.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tutor_materia",
        joinColumns = @JoinColumn(name = "id_tutor"),
        inverseJoinColumns = @JoinColumn(name = "id_materia")
    )
    private List<Materia> materias = new ArrayList<>();

    protected Tutor() {
    }

    public Tutor(String nombre, String correo, String contrasenaHash,
                 String experiencia, double precio, String descripcion) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.experiencia = experiencia;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
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
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                ", materias=" + materias +
                '}';
    }
}