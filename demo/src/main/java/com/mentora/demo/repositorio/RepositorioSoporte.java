
package com.mentora.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentora.demo.modelo.ReporteSoporte;

@Repository
public interface RepositorioSoporte
        extends JpaRepository<ReporteSoporte, Long> {

}
