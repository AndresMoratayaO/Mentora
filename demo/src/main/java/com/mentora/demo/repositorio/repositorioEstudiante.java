package com.mentora.demo.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.Estudiante;

public interface repositorioEstudiante extends JpaRepository<Estudiante, Long> {

    boolean existsByCorreo(String correo);

    Optional<Estudiante> findByCorreo(String correo);
}
