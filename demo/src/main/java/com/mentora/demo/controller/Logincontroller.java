package com.mentora.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentora.demo.repositorio.repositorioEstudiante;
import com.mentora.demo.repositorio.repositorioTutor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class Logincontroller {

    private final repositorioEstudiante estudiantes;
    private final repositorioTutor tutores;
    private final PasswordEncoder encoder;

    /** Datos mínimos de una cuenta, sin importar en qué tabla estaba. */
    private record Cuenta(Long id, String nombre, String contrasenaHash, String tipoUsuario) {
    }

    public Logincontroller(
            repositorioEstudiante estudiantes,
            repositorioTutor tutores,
            PasswordEncoder encoder) {

        this.estudiantes = estudiantes;
        this.tutores = tutores;
        this.encoder = encoder;
    }

    @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> iniciarSesion(
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            HttpServletRequest request) {

        // El registro guarda el correo en minúsculas y sin espacios.
        correo = correo.trim().toLowerCase(Locale.ROOT);

        if (correo.isBlank() || contrasena.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("El correo y la contraseña son obligatorios.");
        }

        Optional<Cuenta> encontrada = buscarCuenta(correo);

        if (encontrada.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No existe una cuenta con ese correo. "
                            + "Puedes crear una nueva.");
        }

        Cuenta cuenta = encontrada.get();

        // BCrypt solo admite 72 bytes; una contraseña más larga nunca pudo registrarse.
        boolean contrasenaValida =
                contrasena.getBytes(StandardCharsets.UTF_8).length <= 72
                && encoder.matches(contrasena, cuenta.contrasenaHash());

        if (!contrasenaValida) {
            return ResponseEntity.status(401)
                    .body("La contraseña es incorrecta.");
        }

        // Sesión nueva en cada inicio de sesión (evita reutilizar una anterior).
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }

        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(ControladorSesion.ATTR_ID, cuenta.id());
        sesion.setAttribute(ControladorSesion.ATTR_NOMBRE, cuenta.nombre());
        sesion.setAttribute(ControladorSesion.ATTR_TIPO, cuenta.tipoUsuario());

        return ResponseEntity.ok("Inicio de sesión correcto.");
    }

    /** Busca primero en estudiantes y luego en tutores. */
    private Optional<Cuenta> buscarCuenta(String correo) {
        return estudiantes.findByCorreo(correo)
                .map(e -> new Cuenta(
                        e.getId(), e.getNombre(),
                        e.getContrasenaHash(), "ESTUDIANTE"))
                .or(() -> tutores.findByCorreo(correo)
                        .map(t -> new Cuenta(
                                t.getId(), t.getNombre(),
                                t.getContrasenaHash(), "TUTOR")));
    }
}
