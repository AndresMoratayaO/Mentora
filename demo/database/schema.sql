-- =====================================================================
-- Mentora - esquema de base de datos (MariaDB)
--
-- Estudiantes y tutores viven en tablas separadas:
--     estudiantes   -> cuentas de estudiantes
--     tutores       -> cuentas de tutores (con experiencia, precio, etc.)
--     tutor_materia -> materias que enseña cada tutor
--     materias      -> catálogo de materias
--
-- application.properties usa ddl-auto=validate: Hibernate NO crea las
-- tablas, así que este script debe correrse ANTES de iniciar la app.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS mentora
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mentora;
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- PASO 0 (SOLO si ya tenías el diseño anterior con "usuarios" + "tutores")
-- Descomenta estas líneas. No borran nada: solo renombran las tablas
-- viejas para que no choquen con las nuevas (el PASO 3 copia los datos).
-- ---------------------------------------------------------------------
-- RENAME TABLE tutor_materia TO tutor_materia_old,
--              tutores       TO tutores_old,
--              usuarios      TO usuarios_old;

-- ---------------------------------------------------------------------
-- PASO 1: crear tablas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS materias (
    id_materia  BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    PRIMARY KEY (id_materia),
    UNIQUE KEY uk_materias_nombre (nombre)
);

CREATE TABLE estudiantes (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100) NOT NULL,
    correo          VARCHAR(254) NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estudiantes_correo (correo)
);

CREATE TABLE tutores (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100) NOT NULL,
    correo          VARCHAR(254) NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    experiencia     VARCHAR(255) NOT NULL,
    precio          DOUBLE       NOT NULL,
    descripcion     VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_tutores_correo (correo)
);

CREATE TABLE tutor_materia (
    id_tutor   BIGINT NOT NULL,
    id_materia BIGINT NOT NULL,
    PRIMARY KEY (id_tutor, id_materia),
    CONSTRAINT fk_tm_tutor
        FOREIGN KEY (id_tutor)   REFERENCES tutores (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tm_materia
        FOREIGN KEY (id_materia) REFERENCES materias (id_materia)
);

-- ---------------------------------------------------------------------
-- PASO 2: materias iniciales (necesarias para que un tutor pueda
-- registrarse: el formulario le pide elegir una). Se pueden editar.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO materias (nombre, descripcion) VALUES
    ('Matemática',            'Álgebra, geometría y aritmética'),
    ('Cálculo',               'Cálculo diferencial e integral'),
    ('Física',                'Mecánica, electricidad y más'),
    ('Química',               'Química general y orgánica'),
    ('Biología',              'Biología celular y general'),
    ('Programación',          'Lógica, algoritmos y lenguajes de programación'),
    ('Estadística',           'Estadística descriptiva e inferencial'),
    ('Comunicación Efectiva', 'Redacción, oratoria y presentaciones'),
    ('Inglés',                'Conversación y gramática');

-- ---------------------------------------------------------------------
-- PASO 3 (SOLO si hiciste el PASO 0): copiar las cuentas del diseño
-- anterior. Descomenta, ejecuta, y cuando verifiques que todo está bien
-- puedes borrar las tablas *_old.
-- ---------------------------------------------------------------------
-- INSERT INTO estudiantes (id, nombre, correo, contrasena_hash)
-- SELECT id, nombre, correo, contrasena_hash
-- FROM usuarios_old
-- WHERE tipo_usuario = 'ESTUDIANTE';
--
-- INSERT INTO tutores (id, nombre, correo, contrasena_hash,
--                      experiencia, precio, descripcion)
-- SELECT u.id, u.nombre, u.correo, u.contrasena_hash,
--        t.experiencia, t.precio, t.descripcion
-- FROM usuarios_old u
-- JOIN tutores_old t ON t.id_usuario = u.id;
--
-- INSERT INTO tutor_materia (id_tutor, id_materia)
-- SELECT id_tutor, id_materia FROM tutor_materia_old;
