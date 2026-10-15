
-- Mentora
-- Tabla de opiniones y valoraciones de tutores
-- Ejecutar después de crear las tablas usuarios y tutores.

CREATE DATABASE IF NOT EXISTS mentora;

USE mentora;

CREATE TABLE IF NOT EXISTS opiniones (
    id_opinion BIGINT NOT NULL AUTO_INCREMENT,
    id_estudiante BIGINT NOT NULL,
    id_tutor BIGINT NOT NULL,
    comentario VARCHAR(1000) NOT NULL,
    valoracion INT NOT NULL,

    PRIMARY KEY (id_opinion),

    CONSTRAINT fk_opinion_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES usuarios(id),

    CONSTRAINT fk_opinion_tutor
        FOREIGN KEY (id_tutor)
        REFERENCES tutores(id_usuario),

    CONSTRAINT chk_opinion_valoracion
        CHECK (valoracion BETWEEN 1 AND 5)
);
