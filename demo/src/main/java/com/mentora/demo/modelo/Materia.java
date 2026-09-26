package com.mentora.demo.modelo;

public class Materia {

    private Long idMateria;
private String nombre;
private String descripcion;

public Materia(Long idMateria, String nombre, String descripcion) {
    this.idMateria = idMateria;
    this.nombre = nombre;
    this.descripcion = descripcion;
}

public Long getIdMateria() {
    return idMateria;
}

public String getNombre() {
    return nombre;
}

public String getDescripcion() {
    return descripcion;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}

@Override
public String toString() {
    return "Materia{" +
            "idMateria=" + idMateria +
            ", nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            '}';
}
    
}
