# Matriz de requisitos — EV2

[README](../README.md) · [Checklist](tareas-y-dependencias.md) · [Pruebas](pruebas.md)

**Corte vigente:** cierre local `feature/cierre-ev2` sobre `develop` en `427f1ef` (PR #2). Fuente y documentación del cierre todavía sin commit/publicación. Esta matriz relaciona los indicadores de la pauta leída con implementación y evidencia; no concede nota ni porcentaje de cumplimiento.

| Indicador / peso | Implementación | Responsables / material | Evidencia y límite |
|---|---|---|---|
| UI y navegación — 15% | Arranque, Perfil, Inicio, Sesión y Resumen reales; acceso secuencial, repaso y Ver resumen tras completar módulo. | A + integración B; guías 8–10. | Flujo comunicado por Felipe en Xiaomi; tests Compose de Ver resumen y apertura de etapa ante escritura pendiente/fallida. Primera celebración con datos vacíos no repetida manualmente. Adaptabilidad ampliada integrada pendiente. |
| Formularios y feedback — 15% | Crear/editar perfil único; errores con mensaje/icono, candidata de cámara y borrador restaurable. | A4 + B3/B6; guías 11/13. | R09/R12/R13: captura, cancelación, reapertura y giro comunicados. Faltan fallos nativos forzados y comprobación independiente de muerte de proceso. |
| Validación centralizada y estado — 10% | PerfilValidator, StateFlow/ViewModels y SavedStateHandle fuera de las pantallas. | B1–B3 + cierre A; guía 11. | Suite JVM de límites, transiciones, restauración y errores tras Guardar; no acredita todos los teclados/configuraciones Android. |
| Animaciones funcionales — 10% | Swipe/retorno, botones equivalentes, respuesta única, feedback y Continuar manual. | A6 + B2; guía 12. | R03/R10 y tests de admisión; no se atribuye prueba física de todos los eventos tardíos. |
| Modularidad y persistencia — 15% | Repositorio/DataStore, perfil, archivos privados, progreso y marco; contenedor por Application. | B1/B2/B4/B6/B7 + cierre A; guías 7/11–13. | 99 unitarias y regresión Android de recreación. Felipe confirma conservación de nick/progreso/marco. Inicio y Resumen esperan el registro confirmado antes de navegar; fallo/reintento de Inicio probados con operación sustituida. No se persiste índice de tarjeta. |
| Colaboración y versiones — 20% | Aportes A incluidos en base; B integrado mediante PR #2; checklist Markdown y cierre local de A. | Ambos; guía 7. | Historia Git inspeccionada. No se atribuye revisión de Claudio del cierre, publicación de esta rama, nueva revisión de PR, copia limpia ni entrega. |
| Dos recursos nativos seguros — 15% | TakePicture/FileProvider privado para ilustración; pulso háptico neutral por elección. | B5/B6 + A8/cierre; guía 13. | Cámara/cancelación/imagen y pulso botón/swipe comprobados según Felipe en Xiaomi. Ajustes hápticos y fallos de cámara/almacenamiento por ampliar. |

## Evidencia actual

- Felipe ejecutó `assembleDebug`, `testDebugUnitTest`, `lintDebug` y `connectedDebugAndroidTest` con éxito.
- Reportes inspeccionados: **99 pruebas JVM y 6 instrumentadas**, sin fallos/errores; **lint: 0 errores, 16 advertencias**.
- Las 6 instrumentadas cubren contenedor/repositorio/ViewModel tras recrear Activity, 2 casos Compose de Ver resumen, 2 de Inicio ante fallo/espera de escritura y contexto de plantilla. No son 6 pruebas completas de todo el producto ni fuerzan errores nativos de DataStore.
- Dispositivo del runner: `25069PTEBG - 16`, Xiaomi según Felipe. API 33 sin ejecución acreditada.
- Registros manuales R09–R15 en pruebas.md. La prueba de reapertura conservó el paso 2: Felipe acepta continuar si la sesión se retiene. No se comprobó el PID, por lo que no acredita recuperación exacta de tarjeta tras muerte real del proceso.

## Decisiones vigentes

- Resumen automático sólo tras el primer cierre confirmado de etapa 3; etapas 1/2 y todos los repasos vuelven a Inicio.
- Ver resumen aparece debajo de etapa 3 al completar el módulo; permite consultar el logro y aplicar/quitar marco después.
- Sesión retenida: puede conservar el paso actual. Nueva sesión: comienza en paso 1; no se persiste el índice de tarjeta.
- Priorización de funcionamiento y comprensión del flujo en una sesión posterior, por petición de Felipe.
- TalkBack avanzado sigue aplazado, no probado; se conservan etiquetas, mensajes/iconos, botones alternativos y adaptación. No se afirma dispensa general de accesibilidad.

## Indicaciones docentes comunicadas por Felipe

- Seguimiento mediante Markdown en vez de Trello; no elimina participación real ni revisión.
- Cámara + háptica aceptadas como pareja de recursos, sin obligación de galería según ese relato.

## Diferencias todavía abiertas

- Caso escrito: menores de 10; propuesta implementada: edad simulada 7–10, pendiente de confirmación docente.
- Consulta educativa previa no realizada. Felipe prioriza técnica y comunica que no se evaluará pedagogía en EV2; no hay dispensa documental ni validación educativa.
- Cuatro dibujos propios incorporados con origen documentado y revisión visual comunicada; traducciones/distractores aún no verificados con fuente lingüística. Falta procedencia de la ilustración física de cámara.

## Entrega y congelamiento

- [x] README actualizado con integrantes, funciones reales, comandos ejecutados y límites.
- [x] Corregir apertura desde Inicio y verificar fallo/espera/reintento con la Route real y operación sustituida, sin navegar prematuramente.
- [ ] Revisar código/documentación del cierre con Claudio y decidir tratamiento de avisos de lint.
- [ ] Publicar rama y PR hacia develop; revisión del otro integrante, sin autoatribuir aprobación.
- [ ] Comprobar copia limpia y ejecución en mínimo API 33.
- [ ] Verificar ausencia de credenciales, permisos de contenido y acceso docente/equipo.
- [ ] Resolver/explicitar diferencias académicas y revisión lingüística pendiente.
- [ ] Confirmar fecha/hora/enlaces AVA. Fechas históricas declaradas: cierre 24/10/2026 y entrega 27/10/2026; no son hora ni plazos vigentes verificados.
- [ ] Entrega grupal y activación individual de ambos según pauta.
- [ ] Congelar la misma versión entregada y respetar el corte de cambios indicado por el docente.

No añadir backend, Retrofit, APK firmado ni contenido extra por suponer que son obligatorios en EV2.
