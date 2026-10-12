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
    public ResponseEntity<Materia> crearMateria(
            @RequestParam String nombre,
            @RequestParam String descripcion) {

        if (repositorioMateria.existsByNombreIgnoreCase(nombre)) {
            return ResponseEntity.badRequest().build();
        }

        Materia materia = new Materia(nombre, descripcion);

        Materia materiaGuardada = repositorioMateria.save(materia);

        return ResponseEntity.ok(materiaGuardada);
    }

    @GetMapping
    public ResponseEntity<List<Materia>> listarMaterias() {

        List<Materia> materias = repositorioMateria.findAll();

        return ResponseEntity.ok(materias);
    }
}