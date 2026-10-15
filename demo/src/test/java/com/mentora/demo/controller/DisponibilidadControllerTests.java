package com.mentora.demo.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.mentora.demo.modelo.Disponibilidad;
import com.mentora.demo.modelo.Usuario;
import com.mentora.demo.repositorio.DisponibilidadRepositorio;
import com.mentora.demo.repositorio.repositoriousuario;

@ExtendWith(MockitoExtension.class)
class DisponibilidadControllerTests {

    @Mock
    private DisponibilidadRepositorio disponibilidadRepositorio;

    @Mock
    private repositoriousuario usuarioRepositorio;

    private DisponibilidadController controller;

    private Usuario tutor;

    @BeforeEach
    void prepararPrueba() {
        controller = new DisponibilidadController(
                disponibilidadRepositorio,
                usuarioRepositorio);

        tutor = new Usuario(
                "Tutor de prueba",
                "tutor@mentora.local",
                "contrasena",
                "TUTOR");
    }

    @Test
    void debeRegistrarHorarioValido() {
        when(usuarioRepositorio.findById(1L))
                .thenReturn(Optional.of(tutor));

        when(disponibilidadRepositorio
                .findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
                        1L))
                .thenReturn(List.of());

        when(disponibilidadRepositorio.save(any(Disponibilidad.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0));

        ResponseEntity<?> respuesta =
                controller.crearDisponibilidad(
                        1L,
                        "Lunes",
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0));

        assertEquals(201, respuesta.getStatusCode().value());

        verify(disponibilidadRepositorio)
                .save(any(Disponibilidad.class));
    }

    @Test
    void debeRechazarHoraFinalAnterior() {
        when(usuarioRepositorio.findById(1L))
                .thenReturn(Optional.of(tutor));

        ResponseEntity<?> respuesta =
                controller.crearDisponibilidad(
                        1L,
                        "Lunes",
                        LocalTime.of(16, 0),
                        LocalTime.of(14, 0));

        assertEquals(400, respuesta.getStatusCode().value());

        assertEquals(
                "La hora final debe ser posterior a la hora inicial.",
                respuesta.getBody());

        verify(disponibilidadRepositorio, never())
                .save(any(Disponibilidad.class));
    }

    @Test
    void debeRechazarHorarioQueSeCruza() {
        Disponibilidad horarioExistente =
                new Disponibilidad(
                        tutor,
                        "Martes",
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0));

        when(usuarioRepositorio.findById(1L))
                .thenReturn(Optional.of(tutor));

        when(disponibilidadRepositorio
                .findByTutor_IdAndDisponibleTrueOrderByDiaAscHoraInicioAsc(
                        1L))
                .thenReturn(List.of(horarioExistente));

        ResponseEntity<?> respuesta =
                controller.crearDisponibilidad(
                        1L,
                        "Martes",
                        LocalTime.of(15, 0),
                        LocalTime.of(17, 0));

        assertEquals(400, respuesta.getStatusCode().value());

        assertEquals(
                "El horario se cruza con otra disponibilidad activa.",
                respuesta.getBody());

        verify(disponibilidadRepositorio, never())
                .save(any(Disponibilidad.class));
    }

    @Test
    void debeRechazarUsuarioQueNoEsTutor() {
        Usuario estudiante = new Usuario(
                "Estudiante de prueba",
                "estudiante@mentora.local",
                "contrasena",
                "ESTUDIANTE");

        when(usuarioRepositorio.findById(2L))
                .thenReturn(Optional.of(estudiante));

        ResponseEntity<?> respuesta =
                controller.crearDisponibilidad(
                        2L,
                        "Lunes",
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0));

        assertEquals(400, respuesta.getStatusCode().value());

        assertEquals(
                "El usuario indicado no es un tutor.",
                respuesta.getBody());

        verify(disponibilidadRepositorio, never())
                .save(any(Disponibilidad.class));
    }
}