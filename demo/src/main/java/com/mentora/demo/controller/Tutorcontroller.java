package com.mentora.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController 
public class Tutorcontroller {
    @GetMapping("/Tutores")
    public List<String> consultar(){
        return List.of("Andres, Ana, Javier");
    }

    

}
