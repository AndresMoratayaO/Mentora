package com.mentora.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.Estudiante;
import com.mentora.demo.modelo.Materia;
import com.mentora.demo.modelo.Tutor;
import com.mentora.demo.repositorio.repositorioEstudiante;
import com.mentora.demo.repositorio.repositorioMateria;
import com.mentora.demo.repositorio.repositorioTutor;

@RestController
public class Registrocontroller {

    private final repositorioEstudiante estudiantes;
    private final repositorioTutor tutores;
    private final repositorioMateria materias;
    private final PasswordEncoder encoder;

    public Registrocontroller(
            repositorioEstudiante estudiantes,
            repositorioTutor tutores,
            repositorioMateria materias,
            PasswordEncoder encoder) {

        this.estudiantes = estudiantes;
        this.tutores = tutores;
        this.materias = materias;
        this.encoder = encoder;
    }

    @PostMapping(
        value = "/registro",
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> registrar(
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("tipoUsuario") String tipoUsuario,
            @RequestParam("contrasena") String contrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            @RequestParam(value = "experiencia", required = false)
                String experiencia,
            @RequestParam(value = "precio", required = false)
                Double precio,
            @RequestParam(value = "descripcion", required = false)
                String descripcion,
            @RequestParam(value = "idMateria", required = false)
                Long idMateria) {

        nombre = nombre.trim();
        correo = correo.trim().toLowerCase(Locale.ROOT);

        if (nombre.isBlank() || nombre.length() > 100) {
            return ResponseEntity.badRequest()
                    .body("El nombre es obligatorio y admite hasta 100 caracteres.");
        }

        if (correo.length() > 254
                || !correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return ResponseEntity.badRequest()
                    .body("Ingresa un correo electrónico válido.");
        }

        if (!tipoUsuario.equals("ESTUDIANTE")
                && !tipoUsuario.equals("TUTOR")) {
            return ResponseEntity.badRequest()
                    .body("Selecciona Estudiante o Tutor.");
        }

        if (contrasena.isBlank() || contrasena.length() < 8) {
            return ResponseEntity.badRequest()
                    .body("La contraseña debe tener al menos 8 caracteres.");
        }

        if (contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            return ResponseEntity.badRequest()
                    .body("La contraseña es demasiado larga.");
        }

        if (!contrasena.equals(confirmarContrasena)) {
            return ResponseEntity.badRequest()
                    .body("Las contraseñas no coinciden.");
        }

        if (estudiantes.existsByCorreo(correo)
                || tutores.existsByCorreo(correo)) {
            return ResponseEntity.status(409)
                    .body("Ya existe una cuenta con ese correo.");
        }

        Tutor tutor = null;

        if (tipoUsuario.equals("TUTOR")) {

            if (experiencia == null
                    || experiencia.isBlank()
                    || experiencia.trim().length() > 255) {
                return ResponseEntity.badRequest()
                        .body("Ingresa tu experiencia: máximo 255 caracteres.");
            }

            if (precio == null
                    || !Double.isFinite(precio)
                    || precio < 0) {
                return ResponseEntity.badRequest()
                        .body("Ingresa un precio válido, mayor o igual a cero.");
            }

            descripcion = descripcion == null
                    ? ""
                    : descripcion.trim();

            if (descripcion.length() > 500) {
                return ResponseEntity.badRequest()
                        .body("La descripción admite hasta 500 caracteres.");
            }

            if (idMateria == null) {
                return ResponseEntity.badRequest()
                        .body("Selecciona una materia.");
            }

            Materia materia = materias.findById(idMateria).orElse(null);

            if (materia == null) {
                return ResponseEntity.badRequest()
                        .body("La materia seleccionada no existe.");
            }

            tutor = new Tutor(
                    nombre,
                    correo,
                    encoder.encode(contrasena),
                    experiencia.trim(),
                    precio,
                    descripcion
            );

            tutor.agregarMateria(materia);
        }

        try {
            if (tutor != null) {
                tutores.saveAndFlush(tutor);
            } else {
                estudiantes.saveAndFlush(new Estudiante(
                        nombre,
                        correo,
                        encoder.encode(contrasena)
                ));
            }

        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409)
                    .body("No se pudo guardar la cuenta. Revisa los datos; "
                            + "el correo podría estar registrado.");
        }

        return ResponseEntity.status(201)
                .body("Cuenta creada correctamente.");
    }
}
