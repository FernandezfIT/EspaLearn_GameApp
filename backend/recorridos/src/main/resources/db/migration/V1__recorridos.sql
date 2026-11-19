-- Función: crear la persistencia F1 sin reemplazar esquemas ni datos existentes.
-- Identidad demo: permite serializar creaciones concurrentes del mismo aprendiz.
CREATE TABLE aprendices (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY
) ENGINE=InnoDB;

-- Catálogo: claves editoriales estables dentro de una versión, todavía sin contenido F2.
CREATE TABLE palabras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    palabra_id VARCHAR(80) NOT NULL,
    version_catalogo VARCHAR(40) NOT NULL,
    texto_espanol VARCHAR(100) NOT NULL,
    CONSTRAINT uq_palabra_version UNIQUE (version_catalogo, palabra_id)
) ENGINE=InnoDB;

-- Recorridos: la columna generada limita a uno ACTIVO incluso ante errores de aplicación.
CREATE TABLE recorridos (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
    aprendiz_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    version_catalogo VARCHAR(40) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    creado_en TIMESTAMP(6) NOT NULL,
    aprendiz_activo_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin
        GENERATED ALWAYS AS (CASE WHEN estado = 'ACTIVO' THEN aprendiz_id ELSE NULL END) STORED,
    CONSTRAINT fk_recorrido_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT uq_activo_por_aprendiz UNIQUE (aprendiz_activo_id),
    CONSTRAINT ck_tipo CHECK (tipo IN ('NORMAL', 'REPASO')),
    CONSTRAINT ck_estado CHECK (estado IN ('ACTIVO', 'COMPLETADO'))
) ENGINE=InnoDB;

-- Selección: conserva tanto las tres palabras como su orden durante todo el recorrido.
CREATE TABLE recorrido_palabras (
    recorrido_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    posicion INT NOT NULL,
    palabra_id BIGINT NOT NULL,
    PRIMARY KEY (recorrido_id, posicion),
    CONSTRAINT uq_palabra_recorrido UNIQUE (recorrido_id, palabra_id),
    CONSTRAINT fk_seleccion_recorrido FOREIGN KEY (recorrido_id) REFERENCES recorridos(id),
    CONSTRAINT fk_seleccion_palabra FOREIGN KEY (palabra_id) REFERENCES palabras(id),
    CONSTRAINT ck_posicion CHECK (posicion BETWEEN 0 AND 2)
) ENGINE=InnoDB;

-- Etapas: vacías en F1; su confirmación HTTP se implementará en las siguientes fases.
CREATE TABLE recorrido_etapas (
    recorrido_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    posicion INT NOT NULL,
    etapa VARCHAR(20) NOT NULL,
    PRIMARY KEY (recorrido_id, posicion),
    CONSTRAINT fk_etapa_recorrido FOREIGN KEY (recorrido_id) REFERENCES recorridos(id),
    CONSTRAINT ck_etapa CHECK (etapa IN ('descubrir', 'con_apoyo', 'menos_apoyo')),
    CONSTRAINT ck_etapa_posicion CHECK (posicion BETWEEN 0 AND 2)
) ENGINE=InnoDB;

-- Idempotencia: una solicitud aceptada identifica el resultado, no vuelve a sortear.
CREATE TABLE solicitudes_inicio (
    id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
    aprendiz_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    solicitud_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    version_catalogo VARCHAR(40) NOT NULL,
    recorrido_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    CONSTRAINT uq_solicitud_aprendiz UNIQUE (aprendiz_id, solicitud_id),
    CONSTRAINT fk_solicitud_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT fk_solicitud_recorrido FOREIGN KEY (recorrido_id) REFERENCES recorridos(id)
) ENGINE=InnoDB;
