package com.mentora.demo.controller;

import com.mentora.demo.modelo.ProgresoAcademico;
import com.mentora.demo.modelo.SolicitudProgresoAcademico;
import com.mentora.demo.repositorio.RepositorioProgresoAcademico;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/progresos")
public class ControladorProgresoAcademico {

    private final RepositorioProgresoAcademico repositorio;

    public ControladorProgresoAcademico(
            RepositorioProgresoAcademico repositorio) {

        this.repositorio = repositorio;
    }

    @PostMapping
    public ResponseEntity<ProgresoAcademico> registrar(
            @Valid @RequestBody SolicitudProgresoAcademico solicitud) {

        ProgresoAcademico progreso = repositorio.guardar(solicitud);

        URI ubicacion = URI.create("/progresos/" + progreso.id());

        return ResponseEntity
                .created(ubicacion)
                .body(progreso);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<ProgresoAcademico> consultarPorEstudiante(
            @PathVariable("estudianteId") long estudianteId) {

        validarIdentificador(estudianteId);

        return repositorio.consultarPorEstudiante(estudianteId);
    }

    @GetMapping("/{id}")
    public ProgresoAcademico consultarPorId(
            @PathVariable("id") long id) {

        validarIdentificador(id);

        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un progreso con ese identificador"
                ));
    }

    private void validarIdentificador(long id) {
        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador debe ser positivo"
            );
        }
    }
}