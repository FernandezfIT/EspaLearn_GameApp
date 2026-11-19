-- Función: mantener la misma identidad exacta de catálogo en Kotlin, Java y MySQL.
-- Versiones: MySQL no debe aceptar una versión distinta sólo por ignorar mayúsculas.
ALTER TABLE palabras
    MODIFY version_catalogo VARCHAR(40) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    MODIFY palabra_id VARCHAR(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL;
ALTER TABLE recorridos
    MODIFY version_catalogo VARCHAR(40) CHARACTER SET ascii COLLATE ascii_bin NOT NULL;
ALTER TABLE solicitudes_inicio
    MODIFY version_catalogo VARCHAR(40) CHARACTER SET ascii COLLATE ascii_bin NOT NULL;
-- No se modifican los identificadores guardados ni se elimina ningún recorrido.
