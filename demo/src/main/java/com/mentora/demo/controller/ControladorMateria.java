package com.mentora.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.Materia;
import com.mentora.demo.repositorio.repositorioMateria;

@RestController
@RequestMapping("/materias")
public class ControladorMateria {

    private final repositorioMateria repositorioMateria;

    public ControladorMateria(repositorioMateria repositorioMateria) {
        this.repositorioMateria = repositorioMateria;
    }

    @PostMapping
    public ResponseEntity<?> crearMateria(
            @RequestParam String nombre,
            @RequestParam String descripcion) {

        String nombreLimpio = nombre.trim();
        String descripcionLimpia = descripcion.trim();

        if (nombreLimpio.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("El nombre de la materia es obligatorio.");
        }

        if (repositorioMateria.existsByNombreIgnoreCase(nombreLimpio)) {
            return ResponseEntity
                    .badRequest()
                    .body("La materia ya existe.");
        }

        Materia materia = new Materia(
                nombreLimpio,
                descripcionLimpia
        );

        return ResponseEntity.ok(
                repositorioMateria.save(materia)
        );
    }

    @GetMapping
    public ResponseEntity<List<Materia>> listarMaterias() {

        return ResponseEntity.ok(
                repositorioMateria.findAll()
        );
    }
}