-- Función: completar las tres cartas de demostración F2; no certificar el inventario final150.
-- Material de prueba: vocabulario inicial y textos editoriales, con dibujos propios existentes/locales.
UPDATE palabras SET apoyo_turco='Ekmek', explicacion='El pan es un alimento que se prepara con harina.',
    imagen_clave='ilustracion_pan', opcion_uno_id='pan_a', opcion_uno_texto='Pan',
    opcion_dos_id='pan_b', opcion_dos_texto='Pera', respuesta_esperada_id='pan_a'
WHERE version_catalogo='prueba-3-v1' AND palabra_id='pan';
UPDATE palabras SET apoyo_turco='Elma', explicacion='La manzana es una fruta que crece en un árbol.',
    imagen_clave='ilustracion_manzana', opcion_uno_id='manzana_a', opcion_uno_texto='Pan',
    opcion_dos_id='manzana_b', opcion_dos_texto='Manzana', respuesta_esperada_id='manzana_b'
WHERE version_catalogo='prueba-3-v1' AND palabra_id='manzana';
UPDATE palabras SET apoyo_turco='Armut', explicacion='La pera es una fruta que puede ser verde o amarilla.',
    imagen_clave='ilustracion_pera', opcion_uno_id='pera_a', opcion_uno_texto='Pera',
    opcion_dos_id='pera_b', opcion_dos_texto='Manzana', respuesta_esperada_id='pera_a'
WHERE version_catalogo='prueba-3-v1' AND palabra_id='pera';
-- No se cambian IDs, selección ni orden de recorridos que ya existían.
