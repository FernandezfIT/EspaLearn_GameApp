# Alcance y decisiones — EV2

[README](../README.md) · [Contratos](contratos.md) · [Ruta B](ruta-b-claudio.md) · [Checklist](tareas-y-dependencias.md)

## Alcance vigente

Acuerdos con Felipe durante la sesión del 15/10/2026; no equivalen a revisión ya realizada por Claudio ni a funciones implementadas.

- Un módulo local, un perfil ficticio y cuatro pantallas reutilizables: Perfil, Inicio, Sesión y Resumen.
- Dos temáticas, sin menú de selección: saludos y alimentos.
- Cuatro unidades: Hola, Adiós, Pan y Manzana.
- Tres etapas secuenciales sobre esas unidades: Descubrir, Con apoyo y Menos apoyo.
- 12 pasos y 8 elecciones; sin nota mínima ni avance automático tras responder.
- Persistir perfil, etapas completadas, etapa activa y preferencia de marco.
- Un logro derivado de completar las tres etapas y marco opcional; sin pantalla adicional.
- Avatar predeterminado, sustituible por captura de ilustración autorizada; sin banco seleccionable en EV2.
- Cámara + háptica neutral: pareja aceptada por el docente según confirmación de Felipe; funcionamiento pendiente de pruebas.

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
- Ambos disponen de teléfono Android físico; modelos/versiones y resultados por registrar al probar.

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

1. Abrir proyecto y leer checklist, contratos y esta nota; no reiniciar planificación K0–K6.
2. Comprobar rama/base de integración y cambios reales antes de editar.
3. Revisar si Claudio entregó B1: tipos, estados y firma del repositorio. Si no, preparar únicamente UI neutral con estado de ejemplo claramente identificado, sin duplicar la lógica de B.
4. Iniciar A1/A2: concretar distribución de las cuatro pantallas y el componente de formulario; después A3/A4 según contratos disponibles.
5. Guiar cada bloque con explicación y práctica; solicitar autorización antes de crear archivos. No añadir funciones fuera del MVP.

Esta sesión es exclusivamente de documentación; no se ha implementado la ruta A ni B.
