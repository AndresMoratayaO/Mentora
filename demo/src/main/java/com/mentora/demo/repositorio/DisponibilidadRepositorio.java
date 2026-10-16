package com.mentora.demo.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.Disponibilidad;

public interface DisponibilidadRepositorio
        extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
            Long tutorId);

    List<Disponibilidad> findByTutor_IdOrderByDiaAscHoraInicioAsc(
            Long tutorId);

    Optional<Disponibilidad> findByIdAndTutor_Id(Long id, Long tutorId);
}
