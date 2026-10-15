# Checklist de tareas y dependencias — EV2

[Volver al README](../README.md) · [Entorno de desarrollo](entorno-desarrollo.md)

**Actualización:** 15/10/2026. **A:** Felipe Fernández. **B:** Claudio Novoa.

## Uso y acuerdos

Este documento reemplaza el tablero externo. Según la indicación del docente comunicada por Felipe el 15/10/2026, no es necesario utilizar Trello ni otra plataforma y se permite un seguimiento simple en Markdown. Esta excepción relatada corresponde al medio de seguimiento, no elimina la necesidad de evidenciar participación real de ambos.

- `[x]`: terminado, con evidencia descrita cuando corresponde.
- `[ ]`: pendiente; puede indicarse **en curso**, **bloqueado** o **preparado con datos de prueba** sin marcarlo como terminado.
- Actualizar la casilla cuando se cumpla el criterio, no al empezar la tarea.
- Añadir referencias a commits, PR o resultados de prueba cuando existan; no inventar evidencia.
- Un componente con datos de prueba no equivale a integración o persistencia real.
- Los responsables son principales, no exclusivos; cualquier reasignación debe quedar anotada.
- No se fijan fechas ni porcentajes ficticios. El calendario se acordará con capacidad real y plazos docentes confirmados.

## E0 — Base reproducible y acuerdos

### Preparación — A

- [x] Crear el proyecto Compose nuevo y ejecutar la pantalla base. Evidencia: ejecución comunicada por Felipe en Pixel 6a emulado, Android 17 / API 37.1.
- [x] Crear organización inicial `ui`, `viewmodel`, `model`, `repository`, `ui/screens` y `ui/components`. Evidencia: captura revisada; los paquetes vacíos no se incluyen en Git hasta contener archivos.
- [x] Revisar `.gitignore`: excluir configuración local, datos generados y `.idea/`.
- [x] Crear README inicial y documentar el alcance previsto.
- [x] Inicializar Git, realizar el primer commit y publicar el proyecto. Evidencia: resultado comunicado por Felipe; [repositorio](https://github.com/FernandezfIT/EspaLearn_GameApp).
- [x] Publicar `main` y `develop`. Evidencia: confirmación de Felipe.
- [x] Enviar invitaciones a Claudio y al docente. Evidencia: confirmación de Felipe; aceptación pendiente.
- [x] Preparar seguimiento Markdown y guía del entorno.
- [x] Publicar checklist y guía del entorno en `develop`. Evidencia: push confirmado por Felipe.
- [ ] Publicar la nueva actualización de contratos y alcance.

### Validación y acuerdos — ambos

- [ ] Confirmar aceptación de las invitaciones.
- [ ] Claudio obtiene una copia, sincroniza, compila y ejecuta. Registrar el resultado en la guía del entorno.
- [ ] Revisar juntos alcance y reparto; confirmar disponibilidad real.
- [ ] Resolver diferencia con la consulta previa exigida por el caso: no realizada y no prevista por Felipe para EV2 por tiempo. Se prioriza técnica; Felipe comunica que pedagogía no se evaluará. Sin dispensa documentada ni validación; ver criterios-pedagogicos.md.
- [x] Confirmar aceptación docente de cámara + háptica como pareja de recursos. Evidencia: confirmación del docente relatada por Felipe; implementación y pruebas pendientes.
- [ ] Confirmar con el docente el rango propuesto 7–10: el caso escrito indica menores de 10. Se avanzará con 7–10 como propuesta y se ajustará si corresponde.
- [ ] Definir contenido, procedencia y revisión lingüística.
- [x] Aprobar con Felipe los comportamientos K0–K6; ver [contratos](contratos.md). Esto no acredita revisión de Claudio ni implementación.
- [x] Reducir EV2 a un módulo de saludos/alimentos: cuatro unidades, 12 pasos y 8 elecciones, sin selector de temática.
- [x] Aprobar reglas técnicas con Felipe: nick NFC/3–20 puntos de código/letras Unicode o 0–9, errores al Guardar y luego al corregir, y bloqueo de Atrás/envíos durante escritura.
- [x] Aprobar base mínima: StateFlow/ViewModel, repositorio, Preferences DataStore, fotos privadas, conexión manual y JUnit4 disponible; sin Hilt, Room ni backend.
- [x] Aprobar cuatro expresiones (Hola, Adiós, Pan, Manzana) y apoyos de etapas, con ayuda turca inline en Menos apoyo.
- [x] Acordar PR pequeños desde feature hacia develop, revisión del otro integrante y main estable.
- [x] Confirmar disponibilidad de teléfono físico de ambos; modelos/versiones y pruebas pendientes.
- [ ] Revisar K0–K6 con Claudio y concretar firmas/owners en el primer PR B1; seguir ruta-b-claudio.md.
- [ ] Materializar los contratos técnicos iniciales compilables.
- [ ] Concretar fronteras técnicas K5/K6: archivo/captura pendiente, recuperación/limpieza e identidad/consumo de señal háptica.
- [ ] Acordar scopes de ViewModels y conexión entre pantallas y estado.
- [ ] Aplicar el flujo de PR acordado y registrar pruebas/revisión reales al implementar.

**Salida técnica E0:** base ejecutable por ambos y contratos iniciales compilables; aún pendiente. Consulta pedagógica no realizada: Felipe decide avanzar técnicamente para EV2 y no prevé realizarla por tiempo. Esa decisión no elimina el requisito escrito ni acredita adecuación educativa. La documentación registra la diferencia en alcance-y-decisiones.md y criterios-pedagogicos.md.

## E1 — Interfaz base y comportamiento en memoria

Dependencias: contratos y decisiones pertinentes de E0. Se permiten estados sintéticos explícitos para preparar componentes, no para declarar integración real.

### A — Felipe

- [ ] **A1:** revisar flujo de Perfil, Inicio, Sesión y Resumen, incluyendo carga, errores, Atrás y cancelación.
- [ ] **A2:** preparar tema y componentes reutilizables con estados y callbacks.
- [ ] **A3:** implementar navegación e Inicio, distinguiendo carga inicial de ausencia de perfil.
- [ ] **A4:** implementar un formulario reutilizable según K1, con mensajes e indicadores por nick vacío/corto/largo/inválido y edad vacía/no entera/fuera de rango. Mostrar guardado/fallos sin perder borrador. Sin botón Cancelar ni selector múltiple; Atrás descarta el borrador según el modo.

### B — Claudio

- [ ] **B1:** implementar modelos, estados, interfaz de repositorio y ejemplos sintéticos acordados.
- [ ] **B2:** implementar ViewModels y transiciones según K3: Descubrir sin pregunta; una elección por botón/swipe, feedback y Continuar manual en práctica. Controlar eventos duplicados o de otra tarjeta; sin temporizador, vidas ni puntuación mínima.
- [ ] **B3:** centralizar validaciones del perfil con reglas acordadas y pruebas de límites.

**Salida E1:** formulario, Inicio y esqueleto de Sesión conectados a lógica real en memoria. Si el repositorio es simulado, debe indicarse expresamente. El formulario no navega como exitoso antes de recibir confirmación de guardado.

## E2 — Recorrido y persistencia real

- [ ] **A5 — A:** implementar presentación y dos configuraciones de práctica del único módulo: cuatro unidades (dos saludos/dos alimentos), 12 pasos, 8 elecciones y feedback con avance manual; sin selector de temática.
- [ ] **A7 — A:** implementar resumen, mapa, logro final y marco opcional, consumiendo progreso confirmado.
- [ ] **B4 — B:** implementar repositorio y persistencia local de perfil, etapas, etapa activa y preferencia de marco; acceso secuencial, repetición sin duplicados y errores de lectura/escritura. Confirmar escritura antes de anunciar éxito; sin historial de respuestas ni estadísticas.
- [ ] **Compartida:** verificar traducciones e ilustraciones de Hola, Adiós, Pan y Manzana; concretar pares de opciones identificadas/respuesta esperada y permisos. Aplicar ayudas ya aprobadas; sin ampliar volumen EV2.
- [ ] **Compartida:** verificar que editar perfil conserva progreso y que repetir no duplica etapas completadas.
- [ ] **Compartida:** verificar reapertura: conservar etapas completas y reiniciar desde el comienzo la etapa interrumpida.

**Salida E2:** recorrido completo y recuperación de datos reales tras reapertura. Un resumen con valores prefijados no cierra esta etapa.

## E3 — Interacción, recursos y robustez

- [ ] **A6 — A:** implementar swipe y animaciones útiles, con botones alternativos y una sola elección admitida por paso.
- [ ] **A8 — A:** integrar presentación de cámara/háptica, cancelación y error; partir de avatar predeterminado y conservar el anterior ante captura cancelada/fallida. Sin selector múltiple en EV2.
- [ ] **A9 — A:** verificar adaptabilidad, texto ampliado, teclado, etiquetas y accesibilidad de acciones.
- [ ] **B5 — B:** integrar un pulso neutral e igual para acierto/error por elección admitida, con botón o swipe. Sin pulso por Continuar, duplicados o nueva presentación; respetar disponibilidad/ajustes y mantener feedback visual, sin configuración extra.
- [ ] **B6 — B:** integrar captura de ilustración como candidata del borrador, confirmada al guardar perfil válido; archivo/URI, FileProvider, recuperación y limpieza segura. Cancelación sin error; fallo con mensaje/reintento y avatar anterior conservado. Sin galería, recorte, filtros ni confirmación adicional.
- [ ] **B7 — B:** verificar rotación, muerte de proceso, escritura fallida, captura pendiente y archivo ausente.
- [ ] **Compartida:** probar los recursos aceptados en dispositivo físico y registrar entorno y resultados.

**Salida E3:** funciones integradas y verificadas, incluidos fallos y cancelaciones. Cámara + háptica aceptadas por el docente según confirmación de Felipe; no confundir esa aceptación con implementación ni pruebas superadas.

## E4 — Pruebas, documentación y entrega

- [ ] **A10 — A:** ejecutar pruebas de UI/navegación/formulario/gestos y corregir fallos visuales.
- [ ] **B8 — B:** ejecutar pruebas de validación/transiciones/datos y registrar pruebas físicas/restauración.
- [ ] **B9 — B:** corregir fallos técnicos y completar instrucciones de versiones, almacenamiento y permisos.
- [x] Redactar base documental de decisiones, contratos, situación pedagógica, contenido/licencias, matriz EV2, plan de pruebas y guía de Claudio. No equivale a traducciones verificadas, licencias resueltas, tests ejecutados ni revisión de Claudio.
- [x] Revisar documentación de cierre con Felipe; aprobación explícita recibida. No acredita revisión de Claudio.
- [ ] **Ambos:** publicar esta actualización y mantener documentos con resultados de implementación y evidencia real.
- [ ] **Ambos:** comprobar requisitos de la pauta y participación de ambos con evidencia de cambios.
- [ ] **Ambos:** comprobar una copia limpia del proyecto y ensayar explicación de aportes.
- [ ] **Ambos:** confirmar fecha/hora AVA, entrega grupal, activación individual y restricciones de cambios posteriores.
- [ ] **Ambos:** congelar y entregar la misma versión en los medios exigidos.

**Salida E4:** proyecto reproducible, requisitos verificados, documentación coherente y entrega confirmada. No se marca entrega sin comprobación real.

## Dependencias cruzadas principales

| Consumidor | Necesita | Puede preparar mientras espera | No puede declarar terminado |
|---|---|---|---|
| A4: formulario | B1/B3: estado y errores; B4: guardado duradero | UI con estados sintéticos identificados | Validación y persistencia sin integración. |
| A5/A6: tarjetas | B2: identidades y transiciones; contenido revisado | Presentación con datos de prueba | Recorrido real y control de duplicados. |
| A7: resumen | B4: progreso confirmado | Estado completado de ejemplo | Recuperación o logro persistido. |
| A8: recursos | B5/B6 y contratos K5/K6; pareja aceptada según confirmación de Felipe | Botones, mensajes y callbacks | Recursos reales sin pruebas. |
| B: lógica/datos | Contratos y reglas acordadas | Validadores, repositorio y tests sin UI final | Experiencia integrada sin recorrido real. |

## Relación con EA2

| Material | Tareas relacionadas |
|---|---|
| Guía 7: estructura y colaboración | E0, reparto y seguimiento continuo. Medio Markdown por indicación docente comunicada. |
| Guía 8: pantalla base y componentes | A2, A4 y A5. |
| Guía 9: adaptabilidad | A9. |
| Guía 10: navegación | A3 y pruebas de recorrido. |
| Guía 11: formulario, validación y estado | A4, B1, B2 y B3. Transferir conceptos al perfil ficticio; no copiar datos personales del ejercicio. |
| Guía 12: estado, persistencia y animaciones | B4, A6 y recuperación. |
| Guía 13: recurso nativo/cámara | B6 y A8; cámara + háptica aceptadas según confirmación de Felipe, implementación y pruebas pendientes. |
| Pauta EV2 | Verificación transversal y E4; prevalece para entrega, salvo excepción docente expresamente registrada. |

## Registro de cambios de planificación

- **15/10/2026:** seguimiento Markdown en lugar de plataforma externa, conforme a indicación docente comunicada por Felipe. Rutas confirmadas: A Felipe / B Claudio. Checklist inicial basado en la planificación E0–E4, sin prometer fechas ni funciones adicionales.
- **15/10/2026, acuerdos K0/K1:** Felipe aprueba arranque y perfil, nick 3–20 con letras Unicode/dígitos 0–9, edad simulada propuesta 7–10 pendiente de confirmación docente, avatar predeterminado y captura sin selector múltiple en EV2. Atrás descarta creación y sale de la actividad, o descarta edición y vuelve a Inicio conservando datos previos. Felipe comunica aceptación docente de cámara + háptica. Selector de avatares queda para evolución futura.
- **15/10/2026, acuerdos K2–K6:** Felipe aprueba alcance mínimo (cuatro unidades, saludos/alimentos, 12 pasos/8 elecciones), feedback y avance manual, progreso secuencial/reanudación por etapa/logro único, foto candidata confirmada al guardar y pulso neutral sólo por elección admitida. Reitera validaciones y mensajes de error del formulario. Ampliación de contenido para EV3 como intención del equipo. Tipos compartidos, revisión de Claudio, implementación y pruebas pendientes.
- **15/10/2026, cierre documental en revisión:** reglas de validación, guardado, contenido/apoyos, base técnica y PR aprobadas; ambos con teléfono físico. Felipe decide omitir consulta pedagógica por tiempo y priorizar técnica, con discrepancia visible. Próxima sesión: ruta A, empezando A1/A2 y revisando entrega B1; no repetir planificación K0–K6. Documentación revisada y aprobada por Felipe al cierre de sesión. Publicación de esta última actualización pendiente; próxima sesión dedicada a ruta A.
