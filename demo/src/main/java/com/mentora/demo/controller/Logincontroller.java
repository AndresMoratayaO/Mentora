package com.mentora.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class Logincontroller {
    @PostMapping(value = "/login",produces = MediaType.TEXT_PLAIN_VALUE)


    public ResponseEntity<String> recibirdatos(
        @RequestParam("correo") String correo,
        @RequestParam("contrasena") String contrasena){

        if (correo.isBlank() || contrasena.isBlank()){
            return ResponseEntity.badRequest().body("El correo y la contraseña son obligatorios.");
        }

        return ResponseEntity.ok("Todo bien");
    }

}
