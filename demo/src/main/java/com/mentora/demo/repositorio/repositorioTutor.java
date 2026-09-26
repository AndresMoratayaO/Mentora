package com.mentora.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.Tutor;

public interface repositorioTutor extends JpaRepository<Tutor, Long> {

    List<Tutor> findByMateriasNombreIgnoreCase(String nombreMateria);

}