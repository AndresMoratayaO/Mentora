
-- MENTORA
-- Modulo de soporte tecnico
-- Ejecutar en MariaDB antes de iniciar Spring Boot.

-- Seleccionar la base de datos del proyecto.
USE mentora;

-- Crear tabla de reportes de soporte.
CREATE TABLE IF NOT EXISTS reportes_soporte (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    asunto VARCHAR(150) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
