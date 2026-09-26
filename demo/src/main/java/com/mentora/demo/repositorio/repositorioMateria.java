package com.mentora.demo.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.Materia;

public interface repositorioMateria extends JpaRepository<Materia, Long> {

    Optional<Materia> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}