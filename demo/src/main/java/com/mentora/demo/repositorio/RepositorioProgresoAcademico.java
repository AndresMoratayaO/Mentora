package com.mentora.demo.repositorio;

import com.mentora.demo.modelo.ProgresoAcademico;
import com.mentora.demo.modelo.SolicitudProgresoAcademico;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RepositorioProgresoAcademico {

    private final Map<Long, ProgresoAcademico> registros =
            new LinkedHashMap<>();

    private long siguienteId = 1;

    public synchronized ProgresoAcademico guardar(
            SolicitudProgresoAcademico solicitud) {

        ProgresoAcademico progreso = new ProgresoAcademico(
                siguienteId++,
                solicitud.estudianteId(),
                solicitud.materia(),
                solicitud.actividad(),
                solicitud.nota(),
                solicitud.fecha(),
                solicitud.observaciones()
        );

        registros.put(progreso.id(), progreso);

        return progreso;
    }

    public synchronized List<ProgresoAcademico> consultarPorEstudiante(
            long estudianteId) {

        List<ProgresoAcademico> resultados = new ArrayList<>();

        for (ProgresoAcademico progreso : registros.values()) {
            if (progreso.estudianteId() == estudianteId) {
                resultados.add(progreso);
            }
        }

        return resultados;
    }

    public synchronized Optional<ProgresoAcademico> buscarPorId(long id) {
        return Optional.ofNullable(registros.get(id));
    }
}