/*
 * Tabla utilizada para almacenar los horarios
 * disponibles de los tutores.
 *
 * IF NOT EXISTS evita errores cuando la tabla
 * ya fue creada anteriormente.
 */
CREATE TABLE IF NOT EXISTS disponibilidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    tutor_id BIGINT NOT NULL,

    dia VARCHAR(15) NOT NULL,

    hora_inicio TIME NOT NULL,

    hora_fin TIME NOT NULL,

    disponible BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_disponibilidad_tutor
        FOREIGN KEY (tutor_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_horario_valido
        CHECK (hora_fin > hora_inicio)
);