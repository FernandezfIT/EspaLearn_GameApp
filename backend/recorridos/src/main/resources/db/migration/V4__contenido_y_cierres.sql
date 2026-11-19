-- Función: añadir contenido para Room y conservar cierres/resultados sin alterar datos F1.
-- Contenido editorial: nullable para no inventar información de entradas antiguas incompletas.
ALTER TABLE palabras
    ADD apoyo_turco VARCHAR(100),
    ADD explicacion VARCHAR(500),
    ADD imagen_clave VARCHAR(100),
    ADD opcion_uno_id VARCHAR(80),
    ADD opcion_uno_texto VARCHAR(100),
    ADD opcion_dos_id VARCHAR(80),
    ADD opcion_dos_texto VARCHAR(100),
    ADD respuesta_esperada_id VARCHAR(80);

-- Cierres: etapa y solicitud únicas por recorrido; payload conserva resultados para feedback F3.
CREATE TABLE cierres_etapa (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
    recorrido_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    etapa VARCHAR(20) NOT NULL,
    solicitud_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    huella VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    resultados_json TEXT NOT NULL,
    CONSTRAINT fk_cierre_recorrido FOREIGN KEY (recorrido_id) REFERENCES recorridos(id),
    CONSTRAINT uq_cierre_etapa UNIQUE (recorrido_id, etapa),
    CONSTRAINT uq_cierre_solicitud UNIQUE (recorrido_id, solicitud_id)
) ENGINE=InnoDB;
