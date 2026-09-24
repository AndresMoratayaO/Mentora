package com.mentora.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Saludocontroller {

    @GetMapping("/saludo")
    public String saludar() {
        return "Hola, Mentora";
    }
}
