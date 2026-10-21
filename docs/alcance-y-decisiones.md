# Alcance y decisiones — EV2

[README](../README.md) · [Contratos](contratos.md) · [Ruta B](ruta-b-claudio.md) · [Checklist](tareas-y-dependencias.md)

## Alcance vigente

**Corte vigente:** `feature/cierre-ev2` sobre `develop` en `427f1ef` (PR #2), con cambios locales sin commit/publicación. UI y lógica integradas; 99 unitarias y 6 instrumentadas aprobadas, lint con 0 errores y 16 advertencias. Cámara, háptica y recorrido comprobados según Felipe en Xiaomi. El corte de A del 16/10 se conserva debajo como histórico, no como estado actual. No equivale a revisión de Claudio del cierre ni a validación educativa.

- Un módulo local, un perfil ficticio y cuatro pantallas reutilizables: Perfil, Inicio, Sesión y Resumen.
- Dos temáticas, sin menú de selección: saludos y alimentos.
- Cuatro unidades: Hola, Adiós, Pan y Manzana.
- Tres etapas secuenciales sobre esas unidades: Descubrir, Con apoyo y Menos apoyo.
- 12 pasos y 8 elecciones; sin nota mínima ni avance automático tras responder.
- Persistir perfil, etapas completadas, etapa activa y preferencia de marco.
- Un logro derivado de completar las tres etapas y marco opcional; sin pantalla adicional.
- Avatar geométrico reutilizado en Perfil/Inicio/Resumen, sustituible por candidata de cámara confirmada al guardar. Captura/cancelación/reapertura comprobadas según Felipe; sin banco seleccionable.
- Cámara + háptica neutral aceptadas por el docente según relato de Felipe; funcionamiento físico comunicado, sin atribuir prueba de todos los fallos/ajustes.
- Resumen automático sólo al primer cierre confirmado de etapa 3. Etapas 1/2 y repasos vuelven a Inicio; Ver resumen aparece debajo de etapa 3 al completar el módulo.
- Sesión retenida por Android puede continuar su paso; nueva instancia empieza desde paso 1. No se persiste índice de tarjeta. La continuidad observada en paso 2 fue aceptada por Felipe; no hubo verificación de PID.
- Dibujos de vocabulario propios en Compose, con origen documentado; traducciones/distractores y validación educativa siguen pendientes.

## Histórico — implementación A preparada el 16/10

Base local `feature/base-ui`, commit `e2c8686`: cuatro pantallas, carga/error de arranque, componentes, swipe con retorno, avatar geométrico/marco, Navigation2.9.7 y demo debug. Inicio cambia entre una/dos columnas según ancho local disponible; no se añadió la API WindowSizeClasses.

Felipe confirmó compilación y pruebas manuales de los escenarios; ver pruebas.md. El asistente no ejecutó build ni suite. MainActivity sigue plantilla, y la demo no guarda perfil, completa pasos, admite respuestas ni abre cámara: comunica solicitudes con datos prefijados. Los modelos `*Ui`/`*Presentacion` de A no sustituyen contratos de B.

**Prioridad explícita:** funcionamiento y pruebas funcionales mínimas. TalkBack y accesibilidad avanzada se aplazan salvo exigencia docente expresa; no se probaron. Se conservan etiquetas, semántica básica, botones alternativos, mensajes y adaptación ya implementados. Felipe comunica flexibilización de complejidad por poco plazo, sin dispensa general documentada. La pauta menciona claridad/accesibilidad en formularios, no una obligación explícita de TalkBack en el texto revisado. Iconos de error por campo y demás básicos de la pauta siguen por revisar.

**Corte de trabajo:** Felipe termina la sesión y espera confirmación de avances B para continuar A. No se agregan funciones para llenar la espera ni se declara toda A terminada.

## Perfil

- Nick: normalización NFC; 3–20 puntos de código; cada uno debe ser letra Unicode o dígito ASCII 0–9. Sin recorte de espacios. Puede ser sólo numérico. Marcas combinantes que permanezcan separadas y no sean letras se rechazan. No se promete aceptación de todas las escrituras ni conteo de grafemas visibles.
- Edad simulada: entero 7–10 inclusive. Se trabajará con este rango como propuesta; confirmar ampliación con docente porque el caso dice menores de 10.
- Errores al pulsar Guardar; después se actualizan al corregir campos. La validación pertenece a la lógica, no a los composables.
- Durante guardado: impedir envíos repetidos y bloquear temporalmente salida mediante Atrás hasta recibir resultado. Fallo conserva borrador y habilita reintento/Atrás; éxito sigue el flujo previsto.
- Atrás antes de guardar: al crear descarta y sale de la actividad; al editar descarta y vuelve a Inicio conservando perfil y progreso previos.
- Fallo de captura: mensaje y reintento sin perder avatar previo. Cancelar cámara no es error. La foto nueva sólo se confirma al guardar perfil válido.

## Configuraciones de la sesión

| Etapa | Presentación | Acción |
|---|---|---|
| Descubrir | Ilustración, expresión española y equivalente turco | Continuar; sin pregunta. |
| Con apoyo | Ilustración y equivalente turco; dos opciones escritas en español | Elegir, ver feedback y Continuar. |
| Menos apoyo | Misma ilustración y dos opciones en español; turco oculto | Elegir; Ver ayuda revela el equivalente dentro de la tarjeta; feedback y Continuar. |

En saludos se usarán escenas ilustradas. No audio, temporizador ni pantalla extra para ayuda. El feedback informa el resultado y permite reconocer la respuesta correcta; sus textos finales se revisarán al incorporar contenido. No atribuir eficacia pedagógica a esta configuración.

## Decisiones de equipo y medio de seguimiento

- A: Felipe Fernández, UI/navegación; B: Claudio Novoa, lógica/datos/recursos.
- Seguimiento en Markdown en lugar de Trello por indicación docente comunicada por Felipe.
- Ramas cortas desde develop; PR pequeños hacia develop con revisión del otro integrante antes de integrar. main recibe versiones estables revisadas. No autoaprobar por falta de disponibilidad.
- Base técnica aprobada: módulo app único, StateFlow/ViewModel, repositorio único, Preferences DataStore, fotos privadas y conexión manual; sin Hilt, Room o backend.
- Ambos disponen de teléfono Android físico según Felipe. El cierre se probó en Xiaomi identificado por runner como `25069PTEBG - 16`; entorno propio de Claudio pendiente de detallar.

## Discrepancias y límites académicos

| Tema | Evidencia/decisión | Estado |
|---|---|---|
| Trello | Guías/pauta piden colaboración; Felipe comunica que docente permite sustituir plataforma por Markdown. | Medio alternativo acordado; mantener evidencia real de ambos. |
| Recursos | Guía 13 practica cámara/galería; Felipe comunica aceptación docente de cámara/háptica. | Aceptado según relato; no implica pruebas superadas. |
| Edad | Caso: menores de 10; propuesta del equipo: 7–10. | Confirmación docente pendiente; no presentar como cumplimiento literal. |
| Consulta pedagógica | Caso exige consulta previa. No realizada; Felipe indica que probablemente no se realizará por tiempo y prioriza aspectos técnicos de EV2. También indica que no se evaluará pedagogía en esta entrega. | Diferencia de alcance abierta; sin dispensa documentada ni validación pedagógica. |

La decisión de avanzar técnicamente no elimina el requisito escrito ni garantiza ausencia de observaciones académicas. No inventar entrevista, aprobación de especialista o evidencia de aprendizaje.

## Fuera de EV2

Selector múltiple de avatares, más unidades, más cursos, selección de temática, historial/estadísticas, login, multiperfil, backend, servicios de traducción/IA, audio, editor de imágenes, galería, timer, vidas, tienda, ranking y modos adicionales.

Ampliar contenido para EV3 es intención del equipo; no se presenta como requisito docente confirmado. Repaso mezclado y Mini diálogos siguen siendo ideas futuras sólo documentales, sin botones de próximamente dentro de la app.

## Reanudación de Felipe — próxima sesión

1. Revisar `git status` y preservar todos los archivos nuevos del cierre. No repetir la integración B ni reconstruir la UI. No hacer commit/push sin instrucción explícita.
2. Inicio y Resumen ya esperan el registro confirmado de etapa antes de navegar. Conservar las regresiones de Inicio ante fallo/reintento y escritura pendiente, ambas aprobadas; no reintroducir el método asíncrono sin resultado.
3. Si se amplía verificación: API 33, primera celebración con datos iniciales vacíos, ajustes hápticos, fallos nativos y muerte de proceso con PID. No confundir continuidad en Recientes con prueba de proceso muerto.
4. Revisar con Claudio, publicar rama/PR, comprobar copia limpia y confirmar requisitos/fechas de entrega en AVA. Mantener discrepancias académicas y traducciones pendientes explícitas.
5. Por petición de Felipe, reservar una sesión posterior para comprender el flujo. Este cierre priorizó desarrollo guiado: el asistente modificó código y Felipe ejecutó comandos y pruebas; no se atribuye comprensión completa por haber aprobado pruebas.

El estado anterior del 16/10 fue sustituido por el cierre descrito en README y pruebas.md. Los cambios del cierre siguen locales; no se ejecutaron commit/push.
