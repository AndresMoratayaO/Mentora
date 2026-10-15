
package com.mentora.demo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "opiniones")
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpinion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Usuario estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tutor", nullable = false)
    private Tutor tutor;

    @Column(nullable = false, length = 1000)
    private String comentario;

    @Column(nullable = false)
    private int valoracion;

    protected Opinion() {
    }

    public Opinion(Usuario estudiante, Tutor tutor,
                   String comentario, int valoracion) {
        setEstudiante(estudiante);
        setTutor(tutor);
        setComentario(comentario);
        setValoracion(valoracion);
    }

    public Long getIdOpinion() {
        return idOpinion;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Usuario estudiante) {
        if (estudiante == null ||
            !"ESTUDIANTE".equalsIgnoreCase(estudiante.getTipoUsuario())) {
            throw new IllegalArgumentException(
                "La opinión debe pertenecer a un estudiante.");
        }
        this.estudiante = estudiante;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        if (tutor == null) {
            throw new IllegalArgumentException(
                "El tutor es obligatorio.");
        }
        this.tutor = tutor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        if (comentario == null || comentario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El comentario no puede estar vacío.");
        }
        this.comentario = comentario.trim();
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        if (valoracion < 1 || valoracion > 5) {
            throw new IllegalArgumentException(
                "La valoración debe estar entre 1 y 5.");
        }
        this.valoracion = valoracion;
    }

    @Override
    public String toString() {
        return "Opinion{" +
                "idOpinion=" + idOpinion +
                ", idEstudiante=" + estudiante.getId() +
                ", idTutor=" + tutor.getIdTutor() +
                ", comentario='" + comentario + '\'' +
                ", valoracion=" + valoracion +
                '}';
    }
}
