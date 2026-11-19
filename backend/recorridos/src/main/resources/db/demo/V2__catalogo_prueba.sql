-- Función: cargar sólo tres identificadores de prueba para validar la vertical F1.
-- Datos: NO son el catálogo editorial revisado de 150 palabras ni contienen arte/traducciones.
INSERT INTO palabras (palabra_id, version_catalogo, texto_espanol) VALUES
    ('pan', 'prueba-3-v1', 'Pan'),
    ('manzana', 'prueba-3-v1', 'Manzana'),
    ('pera', 'prueba-3-v1', 'Pera');
-- Esta migración sólo se habilita con el perfil demo; producción no recibe fixtures.
