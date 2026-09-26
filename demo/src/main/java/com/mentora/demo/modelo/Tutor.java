package com.mentora.demo.modelo;

import java.util.ArrayList;
import java.util.List;

public class Tutor extends Usuario {

    private Long idTutor;
private String experiencia;
private double precio;
private String descripcion;

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
    return idTutor;
}

public String getExperiencia() {
    return experiencia;
}

public double getPrecio() {
    return precio;
}

public String getDescripcion() {
    return descripcion;
}

public void setExperiencia(String experiencia) {
    this.experiencia = experiencia;
}

public void setPrecio(double precio) {
    this.precio = precio;
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
            "idTutor=" + idTutor +
            ", nombre='" + getNombre() + '\'' +
            ", correo='" + getCorreo() + '\'' +
            ", experiencia='" + experiencia + '\'' +
            ", precio=" + precio +
            ", descripcion='" + descripcion + '\'' +
            ", materias=" + materias +
            '}';
}
    
}
