
package com.mentora.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mentora.demo.modelo.Opinion;

public interface repositorioOpinion
        extends JpaRepository<Opinion, Long> {

    List<Opinion> findByTutor_IdOrderByIdOpinionDesc(Long idTutor);

    List<Opinion> findByEstudiante_Id(Long idEstudiante);

}
