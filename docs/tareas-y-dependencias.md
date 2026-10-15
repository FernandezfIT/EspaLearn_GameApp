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
- [x] Preparar seguimiento Markdown y guía del entorno. Archivos locales creados; commit y publicación de esta actualización pendientes.
- [ ] Publicar esta documentación mediante el flujo de revisión que acuerde el equipo.

### Validación y acuerdos — ambos

- [ ] Confirmar aceptación de las invitaciones.
- [ ] Claudio obtiene una copia, sincroniza, compila y ejecuta. Registrar el resultado en la guía del entorno.
- [ ] Revisar juntos alcance y reparto; confirmar disponibilidad real.
- [ ] Realizar y documentar consulta previa con especialista antes de desarrollar la experiencia infantil.
- [ ] Confirmar aceptación docente de cámara + háptica sin galería.
- [ ] Definir contenido, procedencia y revisión lingüística.
- [ ] Acordar contratos iniciales K0–K4: arranque, perfil, contenido, sesión y progreso.
- [ ] Acordar fronteras K5–K6: captura y háptica.
- [ ] Acordar scopes de ViewModels y conexión entre pantallas y estado.
- [ ] Acordar flujo de PR, revisión y validación antes de integrar cambios.

**Salida E0:** base ejecutable por ambos y contratos iniciales compilables. Hasta resolver consulta y alcance, avanzar sólo con organización, entorno y pruebas neutrales; crear el repositorio no valida por sí solo la experiencia educativa.

## E1 — Interfaz base y comportamiento en memoria

Dependencias: contratos y decisiones pertinentes de E0. Se permiten estados sintéticos explícitos para preparar componentes, no para declarar integración real.

### A — Felipe

- [ ] **A1:** revisar flujo de Perfil, Inicio, Sesión y Resumen, incluyendo carga, errores, Atrás y cancelación.
- [ ] **A2:** preparar tema y componentes reutilizables con estados y callbacks.
- [ ] **A3:** implementar navegación e Inicio, distinguiendo carga inicial de ausencia de perfil.
- [ ] **A4:** implementar un formulario reutilizable para crear/editar perfil, mostrando errores por campo y estado de guardado.

### B — Claudio

- [ ] **B1:** implementar modelos, estados, interfaz de repositorio y ejemplos sintéticos acordados.
- [ ] **B2:** implementar ViewModels y transiciones: inicio, elección, feedback, continuación y salida; controlar eventos duplicados o de otra tarjeta.
- [ ] **B3:** centralizar validaciones del perfil con reglas acordadas y pruebas de límites.

**Salida E1:** formulario, Inicio y esqueleto de Sesión conectados a lógica real en memoria. Si el repositorio es simulado, debe indicarse expresamente. El formulario no navega como exitoso antes de recibir confirmación de guardado.

## E2 — Recorrido y persistencia real

- [ ] **A5 — A:** implementar las configuraciones de tarjetas y feedback del recorrido acordado.
- [ ] **A7 — A:** implementar resumen, mapa, logro final y marco opcional, consumiendo progreso confirmado.
- [ ] **B4 — B:** implementar repositorio y persistencia local de perfil, etapas, etapa activa y preferencia de marco; tratar errores de lectura/escritura.
- [ ] **Compartida:** incorporar catálogo e imágenes autorizadas con identificadores estables y revisión lingüística registrada.
- [ ] **Compartida:** verificar que editar perfil conserva progreso y que repetir no duplica etapas completadas.
- [ ] **Compartida:** verificar reapertura: conservar etapas completas y reiniciar desde el comienzo la etapa interrumpida.

**Salida E2:** recorrido completo y recuperación de datos reales tras reapertura. Un resumen con valores prefijados no cierra esta etapa.

## E3 — Interacción, recursos y robustez

- [ ] **A6 — A:** implementar swipe y animaciones útiles, con botones alternativos y una sola elección admitida por paso.
- [ ] **A8 — A:** integrar presentación de cámara/háptica, cancelación, error y alternativa de avatar.
- [ ] **A9 — A:** verificar adaptabilidad, texto ampliado, teclado, etiquetas y accesibilidad de acciones.
- [ ] **B5 — B:** integrar háptica neutral sólo para elecciones admitidas, evitando duplicación por recomposición/restauración y conservando feedback visual.
- [ ] **B6 — B:** integrar captura de ilustración, archivo/URI, FileProvider y limpieza segura sin perder el avatar vigente.
- [ ] **B7 — B:** verificar rotación, muerte de proceso, escritura fallida, captura pendiente y archivo ausente.
- [ ] **Compartida:** probar los recursos aceptados en dispositivo físico y registrar entorno y resultados.

**Salida E3:** funciones integradas y verificadas, incluidos fallos y cancelaciones. La cámara/háptica están condicionadas a la aceptación docente pendiente; si cambia la pareja de recursos, revisar tareas y alcance antes de implementarla.

## E4 — Pruebas, documentación y entrega

- [ ] **A10 — A:** ejecutar pruebas de UI/navegación/formulario/gestos y corregir fallos visuales.
- [ ] **B8 — B:** ejecutar pruebas de validación/transiciones/datos y registrar pruebas físicas/restauración.
- [ ] **B9 — B:** corregir fallos técnicos y completar instrucciones de versiones, almacenamiento y permisos.
- [ ] **Ambos:** completar documentos de decisiones, contratos, criterios pedagógicos, contenido/licencias, matriz EV2 y pruebas con información real.
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
| A8: recursos | B5/B6 y aceptación docente | Botones, mensajes y callbacks | Recursos reales sin pruebas. |
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
| Guía 13: recurso nativo/cámara | B6 y A8; pareja final de recursos pendiente de aceptación. |
| Pauta EV2 | Verificación transversal y E4; prevalece para entrega, salvo excepción docente expresamente registrada. |

## Registro de cambios de planificación

- **15/10/2026:** seguimiento Markdown en lugar de plataforma externa, conforme a indicación docente comunicada por Felipe. Rutas confirmadas: A Felipe / B Claudio. Checklist inicial basado en la planificación E0–E4, sin prometer fechas ni funciones adicionales.
