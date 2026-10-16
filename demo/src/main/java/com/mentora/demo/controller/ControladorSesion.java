package com.mentora.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RestController
public class ControladorSesion {

    public static final String ATTR_ID = "usuarioId";
    public static final String ATTR_NOMBRE = "nombreUsuario";
    public static final String ATTR_TIPO = "tipoUsuario";

    public record SesionDTO(Long id, String nombre, String tipoUsuario) {
    }

    @GetMapping("/api/sesion")
    public ResponseEntity<SesionDTO> sesionActual(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute(ATTR_ID) == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new SesionDTO(
                (Long) sesion.getAttribute(ATTR_ID),
                (String) sesion.getAttribute(ATTR_NOMBRE),
                (String) sesion.getAttribute(ATTR_TIPO)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }

        return ResponseEntity.noContent().build();
    }
}
