package com.mentora.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class Registrocontroller {

    @PostMapping(value ="/registro",produces =  MediaType.TEXT_PLAIN_VALUE)

    public ResponseEntity<String> registrar(
        @RequestParam("nombre") String nombre,
        @RequestParam("correo") String correo,
        @RequestParam("tipoUsuario") String tipoUsuario,
        @RequestParam("contrasena") String contrasena,
        @RequestParam("confirmarContrasena") String confirmarContrasena) {

            String nombretrim = nombre.trim();
            return ResponseEntity.ok("Todo bien");
        }
    
}
