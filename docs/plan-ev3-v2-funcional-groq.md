# EspaLearn EV3 v2 — entrega funcional de Felipe y mejora visual posterior

Corte: 18/11/2026. Plan operativo vigente. Reemplaza el reparto funcional A/B y la integración Wikipedia del plan del 12/11.

**F2 ejecutado — 18/11/2026:** [Vertical Android, ejecución y límites](f2-vertical-android.md). Retrofit/Room/MVVM conectados al servidor; tres etapas con cierre confirmado y reanudación del mismo lote.129unitariasAndroid/9instrumentadas,8unitariasbackend/21integraciones aprobadas; lint0errores/20advertencias. Groq/CRUDcompleto/150/release pendientes. La imagenAPI37 de prueba no publica ACCESS_LOCAL_NETWORK: integración real comprobada, protección de permiso completa pendiente en imagen que la implemente. Próximo F3 sólo con autorización. Los cortes siguientes son históricos.

**F1 ejecutado — 18/11/2026:** [Backend MVC, ejecución y evidencia](f1-backend.md). SpringBoot4.1.2/Java21/MavenWrapper y MySQL8.4.13, crear/consultar recorridos persistentes;8unitarias y14integraciones aprobadas, reinicio/recreación MySQL con lote intacto. Código Android sin cambios; no se afirma CRUD móvil ni Groq integrados. Próximo F2, sólo con autorización. Los apartados que describen el backend como propuesta son del corte de planificación; para implementación actual rige ese informe.

**F0 ejecutado:** [contratos v2.1](contratos-ev3-f0.md) y [verificación](f0-verificacion.md). Base/contratos de dominio listos para F1:114unitarias/6instrumentadas aprobadas, lint0errores/18advertencias. No integración EV3 terminada. Nuevas decisiones: conservar marco legado; emulador; backend Maven+Java21. Estructura siguiente sigue siendo objetivo donde no se indica implementación; dependencias Android se ensayan F2 y Groq real F3.

## 1. Decisiones y límites

- Felipe asume toda la funcionalidad pendiente EV3: Android, Spring Boot, MySQL, Room, Retrofit, Groq, catálogo, pruebas, firma y documentación.
- Claudio pasa después a una fase estética, sobre una versión funcional verificada. Es el reparto indicado por Felipe; no acredita aceptación ni horas de Claudio.
- Felipe confirma que la propuesta Android → Spring → Groq satisface el criterio docente de API externa. No equivale a nota garantizada ni aceptación del resto de la entrega.
- Núcleo conservado: 150 sustantivos concretos revisados, tres por recorrido elegidos por servidor, mismas palabras en Descubrir/Con apoyo/Menos apoyo, swipe o botones, feedback determinista y Continuar manual. Sin nota mínima.
- Se mantienen Room para contenido, DataStore para perfil/preferencias, backend como autoridad del avance, descarte sólo incompleto, reintentos sin sincronización offline, repaso tras 150/150 y logro único tras primer recorrido.
- Wikipedia/MediaWiki se retira. Groq redacta feedback al final de la etapa, no contenido base ni respuestas correctas.
- Se propone conservar imágenes locales/OpenMoji como base provisional. La crítica docente a la integración anterior no confirma una sustitución de todo el arte. No generar imágenes con Groq.
- La estética nueva es extra; accesibilidad mínima, legibilidad, botones visibles y estados de error sí son requisitos de la entrega funcional.

## 2. Base de trabajo comprobada en esta conversación

Proyecto: `/home/fernandezf-it/Escritorio/EspaLearn_turco_espaniol`.
Rama creada por autorización: `feature/Experiencia_integracion`, desde `origin/develop` en `d83b480`. La rama antigua se conservó. Se ejecutó fetch y creación de rama; no commit/push ni código EV3. Main remoto consultado: `3603623`. No se ejecutaron builds/tests nuevos.

No continuar desde la copia `test_app/EspaLearn_GameApp-main`, sin `.git`, ni desde la antigua `feature/base-ui`.

## 3. Ruta funcional única — Felipe

Los bloques son secuenciales por dependencia, no fechas prometidas. El catálogo y pruebas avanzan en paralelo desde F1. F1/F2 usan tres palabras; sólo F5 exige las 150.

| Bloque | Entrega | Criterio de cierre |
|---|---|---|
| F0 — base y contratos | Reproducir EV2, registrar versiones y congelar DTO/JSON/estados; definir identidad, migración, red y política Groq | Build y suite base ejecutados o fallos documentados; contratos consistentes, sin secretos |
| F1 — servidor mínimo (verificado en su alcance) | Spring independiente, MySQL Docker persistente, catálogo de tres, identidad demo, crear/consultar recorrido | Reinicio conserva el mismo lote; API y seed reproducibles; móvil no accede a MySQL |
| F2 — vertical Android (verificada con tres palabras) | Retrofit API propia, Room, preparación, navegación y cierre de las tres etapas sobre el mismo lote | Recorrido completo desde UI real; reabrir recupera activo/resultado; MySQL y Room comprobados |
| F3 — Groq gratuito | Cliente desde Spring, contexto por etapa, panel Android, persistencia, límites y alternativa local | Llamada real visible; duplicados no generan llamadas normales adicionales; fallo IA no bloquea avance |
| F4 — ciclo robusto | Descarte, confirmaciones idempotentes, timeout/reconciliación, progreso, logro único y migración EV2 | C/R/U/D desde Android, sin doble conteo ni pérdida de perfil/historial; reintentos probados |
| F5 — alcance completo | 150 entradas revisadas, imágenes/mappings/créditos, repaso, pruebas y cobertura | Catálogo validado; 150/150 y repaso sin inflación; >=80% lógica móvil medido con denominador explícito |
| F6 — release funcional | APK release firmada, instalada, red real, documentación, evidencias y paquete | ESA APK ejecuta flujo real; firma verificada; fuentes/configuración/artefacto coinciden; sin secretos |
| E1 — extra visual Claudio | Mejoras estéticas pequeñas y regresión sobre la base F6 | No rompe contratos/funcionalidad; si se incorpora a entrega, nueva release firmada y reprobada |

### F0 — decisiones técnicas a concretar antes de consumidores

1. Modelos y JSON de catálogo, recorrido, etapas, resultados, progreso, errores y feedback.
2. Identidad demo estable no equivalente a autenticación; un activo por aprendiz como propuesta.
3. Reanudación al inicio de etapa pendiente como propuesta; no prometer tarjeta exacta.
4. Migración EV2: conservar perfil/avatar/preferencias; no convertir etapas antiguas en palabras completadas ni borrar marco previo silenciosamente. Tratamiento del marco legado pendiente de decisión.
5. Versiones reales Java/Spring, Room/KSP y Retrofit compatibles; no migrar toolchain innecesariamente.
6. Host y dispositivo de demo, HTTPS o excepción HTTP local acotada y permisos según versión efectiva.
7. Verificar cuenta Free Groq y modelo permitido; no pedir la clave en el chat.

### Trabajo paralelo que no se debe posponer

- Inventario editorial desde F1: palabra/sentido, turco, opciones por ID, respuesta, explicación local, imagen, fuentes/licencias, versión y estado de revisión.
- Medir diez entradas antes de estimar las 150. Generar filas no equivale a revisarlas.
- Pruebas nuevas en cada bloque; ensayar herramienta de cobertura pronto.
- Mantener instrucciones de arranque y matriz de evidencia junto a cada incremento.

## 4. Contratos v2 — diseño propuesto para implementar

Estos detalles son propuesta técnica; las decisiones de responsabilidad y aceptación del criterio Groq sí fueron comunicadas por Felipe. No atribuir aceptación de todos los DTO o parámetros al docente/Claudio.

### C1 — contenido y fuentes de verdad

- MySQL: catálogo/versiones, selección persistida, etapas confirmadas, palabras únicas, elegibilidad de logro y feedback guardado.
- Room: contenido editorial revisado por palabra/versión y referencia de imagen; no cola offline ni autoridad de avance.
- DataStore/archivos: identidad demo, perfil, avatar y preferencias pertinentes.
- Recursos Android: imágenes locales; no enteros `R.drawable` como IDs externos.
- Catálogo propio alimenta Room. Eliminar campos obligatorios MediaWiki/pageId/hash de extracto vivo. Conservar fuente editorial/licencias/versionado aplicables.
- IA no sustituye traducciones, alternativas revisadas, corrección determinista ni significado del catálogo.

### C2 — juego y resultados

- Servidor fija tres IDs distintos y su orden antes de jugar. NORMAL excluye completadas; REPASO explícito tras completar el pool no aumenta cobertura.
- Cierre de cada etapa requiere confirmación servidor. Última etapa actualiza cobertura/logro una vez.
- Registrar para práctica: palabraId, opcionElegidaId y uso de ayuda cuando corresponda. Backend valida pertenencia, opciones y orden; no confiar en booleanos de acierto enviados por Android.
- Descubrir registra presentación/completado, no aciertos inventados. No se requiere nota para avanzar.
- Reintentos del mismo cierre usan la misma identidad/solicitud; payload distinto para cierre ya aceptado produce conflicto documentado.
- Volver conserva activo. DELETE sólo incompleto, libera selección y conserva historial/progreso/perfil.

### C3 — HTTP propio

Base propuesta: `/api/v1/aprendices/{aprendizId}`.

| Método/ruta | Semántica |
|---|---|
| POST `/recorridos` | Crear o recuperar activo con solicitudId estable y versión; nunca otro sorteo por respuesta perdida |
| GET `/recorridos/activo` | Activo o ausencia explícita, distinta de error |
| GET `/recorridos/{id}` | Recuperar/reconciliar estado |
| PUT `/recorridos/{id}/etapas/{etapaId}` | Cerrar con resultados validados; repetición coherente no duplica avance |
| DELETE `/recorridos/{id}` | Borrado real de incompleto, 409 si completado; reintento de ausente con respuesta documentada |
| GET `/progreso` | Cobertura única, total, activo y logro confirmado |
| POST `/recorridos/{id}/etapas/{etapaId}/feedback` | Solicitar o devolver generación existente; sólo para etapa confirmada |
| GET `/recorridos/{id}/etapas/{etapaId}/feedback` | Leer estado/texto persistidos; nunca generar por una lectura |
| GET `/catalogo?version=...` | Propuesta: recuperar contenido editorial validado para preparar Room |

Errores con código estable y mensaje; distinguir validación, conflicto, ausencia, red, timeout y fallo servidor. Timeout de cierre implica resultado incierto: reconciliar, no anunciar fracaso definitivo ni crear otro recorrido.

### C4 — Groq aislado del avance

Flujo: cerrar etapa → confirmar MySQL → solicitar feedback → Spring compone contexto mínimo → Groq por HTTPS → validar/persistir → presentar → Continuar. No mantener transacciones SQL abiertas durante la inferencia.

- Android llama con Retrofit sólo al backend propio. La clave Groq permanece en variable de entorno del backend, nunca APK, Git, capturas ni logs.
- Contexto: tres palabras y significados revisados, etapa y resultados mínimos ya validados. No enviar nick, foto, edad, identificadores de aprendiz/recorrido ni datos reales de menores.
- Propuesta de mensaje: frase breve en español que refuerza vocabulario y, donde existan elecciones, una confusión observada; tono amable, sin juicio de dominio. Sin chat ni texto libre del usuario.
- Prompt versionado y modelo configurable. Persistir modelo, versión de prompt, origen, mensaje, estado y fecha junto a etapa; no guardar razonamiento interno del modelo.
- Clave única lógica `(recorrido, etapa, versionFeedback)`; reserva atómica evita llamadas concurrentes normales duplicadas. Consultas/recomposición/reapertura no regeneran.
- Una respuesta incierta del proveedor puede haber consumido cuota: no prometer exactamente una inferencia ante fallos distribuidos. No repetir automáticamente sin límite.
- Validar salida vacía, longitud, formato y reglas de contenido; esas comprobaciones no garantizan exactitud pedagógica ni seguridad absoluta. Usar alternativa editorial ante salida rechazada.
- Estados candidatos: PENDIENTE, GENERANDO, DISPONIBLE, ALTERNATIVO. Respuesta incluye origen GROQ/LOCAL. Etiquetar texto IA y alternativa honestamente.
- UI permite continuar/omitir espera tras avance confirmado. Timeout, cuota, fallo persistencia del feedback o IA nunca revierten etapa ni provocan nuevo cierre.
- Si se descarta un incompleto, cancelar/inutilizar feedback pendiente ligado al recorrido; una respuesta tardía no debe resucitarlo.

### C5 — coste cero como restricción

- Usar cuenta Free, no activar plan Developer de pago ni proveedor alternativo de pago.
- Modelo candidato de la investigación: `openai/gpt-oss-20b`; confirmar disponibilidad/límites reales de la cuenta al implementar, sin fijar cuotas públicas como garantía permanente.
- Una generación normal por etapa (hasta tres por recorrido); reutilizar resultados guardados, sin historial conversacional.
- Configurar presupuesto diario interno de peticiones/tokens, concurrencia, timeout y máximo de salida antes de habilitar la integración; valores finales pendientes de prueba. Superar presupuesto interno produce alternativa local.
- Ante 429 respetar Retry-After y evitar bucles; sin escalado automático a pago.
- Limitar el endpoint y proteger el backend de demo. UUID no es autenticación; no publicar un proxy Groq abierto. Cualquier exposición fuera de LAN controlada requiere diseño de acceso adicional.
- Probar integración real con presupuesto acotado; tests automáticos normales usan dobles, no consumen la API en cada build.
- No garantizar servicio gratuito ilimitado, disponibilidad permanente ni puntos extra. Alternativa local no acredita por sí sola consumo de Groq para evaluación.

## 5. Estructura objetivo — no directorios implementados

```text
EspaLearn_turco_espaniol/
├── app/                         # Android existente, sin reescribirlo
│   └── src/main/java/com/example/espalearn/
│       ├── data/remote/         # Retrofit/DTO API propia, no clave Groq
│       ├── data/local/          # Room contenido editorial
│       ├── repository/          # Recorridos, contenido, feedback
│       ├── model/               # Modelos/estados y resultados
│       ├── viewmodel/           # Preparación/sesión/feedback
│       ├── navigation/          # Recorrido+etapa y reanudación
│       └── ui/                  # Reutilizar; añadir panel feedback
├── backend/recorridos/           # Spring independiente del build Android
│   └── src/main/.../
│       ├── recorrido/           # Controller/service/repository/DTO
│       ├── catalogo/
│       ├── progreso/
│       ├── feedback/            # Orquestación, validación, deduplicación
│       ├── integration/groq/    # Cliente HTTP/configuración externa
│       └── config/
├── infra/                       # MySQL Compose/ejemplos sin secretos
└── docs/                        # Contratos, ejecución, evidencia, créditos
```

Sin microservicios adicionales, agentes, chat, streaming ni framework IA obligatorio. No incorporar Spring al módulo Android. Preferir un cliente HTTP soportado por la versión Spring elegida.

## 6. Calidad y entrega funcional

- Unitarias de selección, fases, resultados, idempotencia, mapeos, ViewModels, presupuestos y alternativas de feedback.
- MySQL objetivo: persistencia, rollback, concurrencia, cierre/descarte y reintentos.
- Room real instrumentado: esquema/DAO/unicidad/actualización/reconstrucción de contenido.
- Groq: doble en tests para éxito, 429, 401, timeout, salida inválida y simultaneidad; evidencia separada de llamada real sin secretos.
- Android: perfil/cámara/avatar/háptica/swipe/botones/adaptabilidad y no revelación anticipada de respuesta.
- >=80% lógica móvil con métrica/denominador acordados e informes, no número de tests ni promedio con backend.
- Release firmada, apksigner, instalación y prueba de esa APK en red/dispositivo objetivo; verificar permisos aplicables al target efectivo.
- Canal privado de llave académica, fecha/hora AVA, freeze y Trello/dispensa EV3 pendientes. No heredar dispensa EV2.

## 7. Estética posterior — Claudio

Entrada: F6 con versión identificable, checklist y APK funcional de respaldo. No significa que AVA esté entregado ni que la colaboración EV3 esté cerrada.

Trabajo: paleta, tipografía, espaciado, jerarquía, ilustraciones y microanimaciones con accesibilidad. Mantener eventos, identidad de recorrido/paso, ayudas, estados de carga/error y Continuar manual. No introducir nuevas reglas, APIs o cambios de esquema por estética sin propuesta explícita.

Cambios pequeños, revisión y pruebas de regresión. No bloquear el respaldo funcional por una mejora visual. Si hay cambios visuales en el artefacto final, volver a generar, firmar, instalar y probar esa versión antes del freeze. Tras el corte docente no modificar entrega.

**Riesgo académico:** la matriz incluye colaboración/aportes de ambos en EV3. El nuevo reparto no elimina ese requisito. Si Claudio participa antes del freeze, sus cambios/revisiones reales pueden aportar evidencia; si participa después, no contarlos retrospectivamente. Confirmar con docente cómo evaluar ese reparto si la estética se deja fuera de entrega.

## 8. Capacidad y siguiente acción

Las cifras antiguas A25–45h/B35–58h ya no describen el reparto. No sumarlas como promesa: Wikipedia sale, Groq y toda la integración pasan a Felipe. Disponibilidad previa Felipe5h/semana no se actualizó; fecha exacta y horas actuales pendientes. No prometer viabilidad ni reducir 150/Room por cuenta propia.

Próxima ejecución: F0, verificar base EV2 y concretar contratos mínimos; después F1/F2 con tres palabras. No comenzar por rediseño ni por 150 archivos vacíos. Medir vertical y lote editorial de diez para recalcular carga. El plan de estudio condensado para presentación se diseña después sobre la implementación real.

## 9. Fuentes técnicas consultadas para la propuesta Groq

- https://console.groq.com/docs/rate-limits
- https://console.groq.com/docs/models
- https://console.groq.com/docs/billing-faqs
- https://console.groq.com/docs/your-data

Consulta directa en esta conversación. No es nueva verificación independiente por researcher ni validación pedagógica del contenido generado.
