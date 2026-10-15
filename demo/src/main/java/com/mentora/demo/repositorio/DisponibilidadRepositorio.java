package com.mentora.demo.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.Disponibilidad;

public interface DisponibilidadRepositorio
        extends JpaRepository<Disponibilidad, Long> {

    /*
     * Obtiene todos los horarios de un tutor,
     * incluyendo los que ya no están disponibles.
     */
    List<Disponibilidad>
            findByTutor_IdOrderByDiaAscHoraInicioAsc(Long tutorId);

    /*
     * Obtiene solamente los horarios activos
     * que pueden mostrarse a los estudiantes.
     */
    List<Disponibilidad>
            findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
                    Long tutorId);

    /*
     * Busca un horario específico y comprueba
     * que realmente pertenezca al tutor indicado.
     */
    Optional<Disponibilidad>
            findByIdAndTutor_Id(Long id, Long tutorId);
}