package com.mentora.demo.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mentora.demo.modelo.Tutor;

public interface repositorioTutor extends JpaRepository<Tutor, Long> {

    boolean existsByCorreo(String correo);

    Optional<Tutor> findByCorreo(String correo);

    /** Busca por nombre del tutor o por nombre de una de sus materias (contiene, sin distinguir mayúsculas). */
    @Query("select distinct t from Tutor t left join t.materias m "
            + "where lower(t.nombre) like lower(concat('%', :texto, '%')) "
            + "or lower(m.nombre) like lower(concat('%', :texto, '%')) "
            + "order by t.nombre")
    List<Tutor> buscar(@Param("texto") String texto);
}