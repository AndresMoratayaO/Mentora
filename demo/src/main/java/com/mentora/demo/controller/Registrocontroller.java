package com.mentora.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.modelo.Usuario;
import com.mentora.demo.repositorio.repositoriousuario;

@RestController
public class Registrocontroller {

    private final repositoriousuario repositorio;
    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public Registrocontroller(repositoriousuario repositorio) {
        this.repositorio = repositorio;
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
            @RequestParam("confirmarContrasena") String confirmarContrasena) {

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

        // BCrypt admite como máximo 72 bytes.
        if (contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            return ResponseEntity.badRequest()
                    .body("La contraseña es demasiado larga.");
        }

        if (!contrasena.equals(confirmarContrasena)) {
            return ResponseEntity.badRequest()
                    .body("Las contraseñas no coinciden.");
        }

        if (repositorio.existsByCorreo(correo)) {
            return ResponseEntity.status(409)
                    .body("Ya existe una cuenta con ese correo.");
        }

        Usuario usuario = new Usuario(
                nombre,
                correo,
                encoder.encode(contrasena),
                tipoUsuario
        );

        try {
            repositorio.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409)
                    .body("No se pudo guardar la cuenta. Revisa los datos; "
                            + "el correo podría estar registrado.");
        }

        return ResponseEntity.status(201)
                .body("Cuenta creada correctamente.");
    }
}