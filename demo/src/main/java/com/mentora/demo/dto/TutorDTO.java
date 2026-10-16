package com.mentora.demo.dto;

import java.util.List;

import com.mentora.demo.modelo.Tutor;

/**
 * Lo que la API devuelve de un tutor.
 * Nunca incluye el correo ni el hash de la contraseña.
 */
public record TutorDTO(
        Long id,
        String nombre,
        String experiencia,
        double precio,
        String descripcion,
        List<MateriaDTO> materias) {

    public record MateriaDTO(Long idMateria, String nombre) {
    }

    public static TutorDTO desde(Tutor tutor) {
        List<MateriaDTO> materias = tutor.getMaterias().stream()
                .map(m -> new MateriaDTO(m.getIdMateria(), m.getNombre()))
                .toList();

        return new TutorDTO(
                tutor.getId(),
                tutor.getNombre(),
                tutor.getExperiencia(),
                tutor.getPrecio(),
                tutor.getDescripcion(),
                materias);
    }
}
