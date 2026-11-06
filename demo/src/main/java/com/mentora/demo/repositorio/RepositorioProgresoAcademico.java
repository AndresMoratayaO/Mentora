package com.mentora.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.ProgresoAcademico;

public interface RepositorioProgresoAcademico
        extends JpaRepository<ProgresoAcademico, Long> {

    List<ProgresoAcademico> findByEstudianteIdOrderByFechaDescIdDesc(Long estudianteId);
}