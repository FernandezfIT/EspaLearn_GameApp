# Contratos EV3 F0 — v2.1

18/11/2026. Base funcional EV2 preservada. Las clases `domain/ev3/ContratosEv3.kt` y sus pruebas son contratos compilables aislados: aún no hay backend, Retrofit, Room ni llamadas Groq conectados a runtime.

Plan: [EV3 v2](plan-ev3-v2-funcional-groq.md). Este documento concreta F0 y prevalece sobre propuestas técnicas anteriores donde difiera. Las decisiones de legado/emulador/Maven fueron elegidas por Felipe; los detalles de implementación restantes son diseño del asistente sujeto a pruebas en F1–F3.

> Actualización F2: [vertical y contratos implementados](f2-vertical-android.md). Android/Retrofit/Room conectados, GETcatalogo y GETprogreso (ambos con query `version`) y PUTetapa implementados/probados. Reintento de cierre conserva UUID y resultados; otro UUID para una etapa ya cerrada produce409. Groq/DELETE/repaso/release aún pendientes. El corte F0 siguiente no representa el estado actual de implementación.

> Actualización F1: [implementación y verificación](f1-backend.md). POSTcrear/recuperar, GETactivo y GETporID ya implementados; SQL/Flyway, deduplicación y concurrencia probados con MySQL real. Las referencias siguientes a backend aún no implementado corresponden al corte F0. El resto de endpoints continúa pendiente; no se conectó Android.

## 1. Decisiones congeladas

- Backend independiente: Java21, Maven, Spring Boot4.1.2 (parent disponible y requisitos oficiales consultados); no incluir en settings.gradle.kts de Android.
- MySQL8.4 como familia propuesta para F1, volumen persistente y puerto sólo loopback. Pin exacto/digest y arranque se verificarán en F1, no se declara imagen descargada.
- Dispositivo principal elegido: emulador Android Studio. Backend host `http://10.0.2.2:8080`; MySQL nunca accesible directamente desde Android. `localhost` dentro del emulador no es el PC.
- Red HTTP de demo acotada al host. Configuración/permiso efectivo por SDK se implementan y prueban en F2/release, no habilitar cleartext global ahora. INTERNET y protección LAN aplicable se verifican con dispositivo real objetivo.
- Una identidad UUID demo estable persistida en DataStore, independiente de nick/edad; no es autenticación. Si falla lectura, no sustituirla por otra silenciosamente. Borrar datos/reinstalar no promete recuperar historial.
- Un recorrido activo por identidad. NORMAL selecciona tres IDs distintos no completados; REPASO sólo tras cobertura total y nunca infla conteos.
- Reanudación tras nueva instancia: principio de etapa pendiente, no tarjeta exacta. Tres palabras y orden fijos.
- Mantener perfil, edad simulada, avatar/archivo privado y preferencias. Nunca enviar esos datos a Groq.
- Marco EV2: conservar desbloqueo si módulo antiguo completo y conservar preferencia aplicada. Registrar `marcoLegadoDesbloqueado` y migración versionada local atómica; no derivarlo sólo del marco aplicado. Recompensa obtenida y desactivada también cuenta como heredada.
- Legado no crea palabras completadas, recorrido remoto ni primer recorrido EV3. Marco disponible = legado o primer recorrido EV3 confirmado. El primer recorrido EV3 se registra aunque ya hubiera marco, sin duplicar recompensa visual.
- No implementar migración destructiva ni borrar progreso EV2 para establecer autoridad remota. Las claves antiguas pueden conservarse como legado, pero no mandar sobre progreso EV3. La función pura de migración ya existe; escritura DataStore se implementará y probará en F2/F4.

## 2. Identificadores, representación y catálogo

- JSON camelCase. UUID para aprendiz/recorrido/solicitud en implementación. Los ejemplos usan IDs sintéticos claramente identificados.
- Etapa wire: `descubrir`, `con_apoyo`, `menos_apoyo` (compatibles con `Etapa.id`). Otros enums: valores mayúsculos definidos en Kotlin. El serializador futuro debe mapear Etapa explícitamente; no confiar en su nombre enum por defecto.
- PalabraId e IDs de opción estables, no índices ni textos traducidos. Imagen por clave estable, no `R.drawable` remoto.
- `versionCatalogo` fija contenido durante un recorrido. Respuesta incluye catálogo válido o endpoint versionado que permita preparar todas sus palabras. Versión inexistente/incompatible = conflicto explícito, no sustitución silenciosa.
- Modelo editorial: palabraId, versionCatalogo, textoEspanol, apoyoTurco, explicacion, imagenClave, dos opciones distintas, respuestaEsperadaId perteneciente a opciones.
- Fuentes/licencias/revisión editorial se mantienen en inventario versionado; sólo entradas aprobadas pueden habilitarse en servidor. F0 no certifica ninguna traducción, imagen ni las 150 entradas.
- Las respuestas correctas son accesibles al cliente para feedback determinista local; no mostrarlas antes de elegir. Backend vuelve a validar sin confiar en `acierto` del cliente.
- Room conserva contenido editorial por palabra/versión. No guarda avance como autoridad ni cola offline. F0 no añade MediaWiki ni campos obligatorios de Wikipedia.
- Catálogo fixture de tres y total3 sólo para integración/pruebas; la entrega exige exactamente150. Contar palabras únicas al cierre final NORMAL, de tres en tres. Cobertura no es dominio.

## 3. HTTP de recorridos

Base `/api/v1/aprendices/{aprendizId}`. El backend de demo sólo se expone en entorno controlado; los UUID no evitan suplantación. Exposición fuera de ese entorno exige control de acceso adicional.

| Método/ruta | Petición / respuesta esperada |
|---|---|
| POST `/recorridos` | `{solicitudId,tipo,versionCatalogo}` → 201 nuevo/200 existente coherente, cuerpo RecorridoEv3 |
| GET `/recorridos/activo` | 200 RecorridoEv3 o 204 sin activo; error HTTP no se transforma en ausencia |
| GET `/recorridos/{id}` | 200 RecorridoEv3; 404 si no pertenece a identidad o no existe |
| PUT `/recorridos/{id}/etapas/{etapaId}` | CierreEtapaEv3 → 200 RecorridoEv3 confirmado; etapa body debe coincidir con ruta |
| DELETE `/recorridos/{id}` | 204 eliminado/ya ausente; 409 si completado; no borrar historial ajeno |
| GET `/progreso` | 200 ProgresoEv3; activo se consulta por endpoint separado |
| GET `/catalogo?version=...` | Propuesta global `/api/v1/catalogo`, 200 `{versionCatalogo,palabras:[...]}`; no requiere identidad ni genera IA |

POST y PUT llevan solicitudId persistido para reintentos de la misma operación. Igual solicitud con diferente contenido/tipo/versión:409. Un activo incompatible con nuevo tipo/versión:409, nunca sorteo alternativo. Si ya existe activo compatible, recuperar su identidad. Estrategia SQL y retención de deduplicación se implementan/prueban en F1/F4, no se resuelven sólo con UUID ni `@Transactional`.

En PUT repetir el mismo cierre aceptado (mismo solicitudId y resultados) devuelve éxito coherente incluso si ya avanzó la etapa. F2 rechaza otro solicitudId para una etapa ya cerrada, aunque el resto del contenido coincida. Distintos resultados para una etapa cerrada:409. El validador de dominio F0 acepta sólo un cierre nuevo sobre etapa pendiente; la capa backend debe resolver reintentos aceptados ANTES de esa validación.

Cierre contiene exactamente tres resultados en orden del lote; versión coincide. Descubrir: opcionElegidaId=null y ayudaUsada=false. Práctica: opción existente por palabra; backend calcula acierto. ayudaUsada sólo representa botón Ver ayuda en Menos apoyo; apoyo siempre visible en Con apoyo no es un uso de ayuda.

El servidor confirma sólo tras commit SQL. Sin transacción abierta mientras juega el alumno o responde Groq. Fallo/timeout de cierre no permite navegar por éxito anticipado: conservar solicitud y consultar/reintentar.

Error JSON: `{codigo,mensaje,campos:{}}`. 400 estructura inválida,404 recurso ausente/no perteneciente,409 conflicto de fase/versión/idempotencia,429 límite propio,503 dependencia no disponible. No devolver credenciales, SQL ni cuerpo crudo de error Groq. Mensajes internos del proveedor no se presentan directamente al niño.

## 4. HTTP y contrato de feedback

Rutas bajo el mismo aprendiz/recorrido:

- POST `/recorridos/{id}/etapas/{etapaId}/feedback`: sólo etapa confirmada; solicitudId estable en body. 200 si terminal persistido,202 si PENDIENTE/GENERANDO. El backend fija versionFeedback activa; el cliente no puede incrementar versión para forzar consumo.
- GET misma ruta: 200 estado guardado;404 si no solicitado. Nunca genera IA por lectura.
- GET no requiere polling infinito. Al reabrir consulta; si estado en curso, permite seguir sin esperar. Recuperación de trabajos estancados debe terminar en alternativa local tras plazo configurado, no lanzar inferencias sin control.
- FeedbackEtapaEv3: estado/origen/mensaje/versionFeedback. DISPONIBLE requiere origen GROQ y frase no vacía. ALTERNATIVO requiere LOCAL y frase revisada. PENDIENTE/GENERANDO no llevan origen ni mensaje.
- Una frase española, máximo240 puntos de código; validar tamaño en backend. Propuesta de presupuesto inicial: prompt de entrada limitado, máximo256 tokens de salida, timeout8s, concurrencia1 y máximo100 intentos/día del servidor. Son techos internos iniciales, NO cuotas garantizadas de Groq ni garantía de finalización del modelo; calibrar con la cuenta en F3.
- Reserva única lógica `(recorrido,etapa,versionFeedback)` antes de inferencia. Capturar versión/modelo del trabajo; persistir sin cambiar el cierre. Respuesta tardía tras descarte no resucita recorrido.
- Contexto construido desde catálogo/resultados validados: palabras/definiciones, etapa y opciones/confusiones pertinentes. No perfil, foto, edad ni UUID enviados al proveedor. Sin texto libre, chat o generación de imágenes.
- Modelo candidato `openai/gpt-oss-20b`, configurable según disponibilidad Free. Nunca escalar automáticamente a pago ni intentar eludir cuotas.
- Sin reintentos automáticos al proveedor por defecto: timeout puede haber consumido cuota. Ante429 respetar Retry-After para próximas solicitudes admitidas; el usuario actual recibe alternativa local. Presupuesto interno agotado/sin clave/salida inválida también produce alternativa identificada, sin falsificar evidencia de IA.
- Llevar contadores de solicitudes/tokens y margen bajo límites reales de organización; no basta máximo100/día ante ráfagas. Presupuesto y concurrencia se hacen cumplir en servidor, no UI.
- Guardar modelo/prompt/origen para trazabilidad técnica; no razonamiento interno ni secretos. Validación de longitud/formato y prompt no garantizan corrección pedagógica absoluta.
- La IA no determina nota, dominio, logro, cobertura ni si el usuario puede continuar.

## 5. Estado de UI a implementar en F2/F3

Preparación: Cargando → Listo(tres cartas válidas) / Error(reintentar). No interpretar carga/error como falta de perfil o de activo.

Cierre: Jugando → Confirmando → Confirmado / Fallo recuperable. Sólo Confirmado puede abrir feedback/siguiente etapa. La clave VM/evento incluirá recorrido+etapa+paso, no sólo etapa como EV2.

Feedback, tras cierre confirmado: PENDIENTE/GENERANDO → DISPONIBLE o ALTERNATIVO; Continuar siempre habilitable sin esperar a IA. Botón/swipe, feedback por elección y háptica actuales se conservan.

## 6. Puertos F0 e integración futura

`RecorridosEv3`, `ContenidoEv3` y `FeedbackEv3` son interfaces suspend compilables sin Retrofit/Room. Implementaciones deben mapear HTTP y validaciones a errores de dominio antes de UI; cancelación de coroutines no se transforma en fallo de negocio. Contratos de transporte finales/serialización se prueban contra fixtures JSON antes de conectar runtime.

`ContratosEv3Test` usa tres palabras sintéticas para validar orden, opciones, versiones, estados, cobertura y legado. No son un FakeBackend ni pruebas de persistencia/concurrencia real. Las listas de DTO deben materializarse como instantáneas no mutables al construir repositorios.

Room/KSP, Retrofit/conversor y mecanismo de cobertura no se fijan a ciegas: resolver y ensayar versiones sobre AGP9.3.3/Kotlin Compose2.2.10 en F2, antes de consumidores reales. No migrar JUnit4 ni toolchain como efecto colateral. No se afirma compatibilidad de dependencias todavía ausentes.

## 7. Pendientes explícitos para siguientes bloques

- F1: arrancar Spring/MySQL, fijar imagen/versiones efectivas y restricciones/idempotencia SQL; wrapper Maven reproducible.
- F2: dependencias Android compatibles, Room y migración DataStore atómica; configuración red/permisos y pruebas instrumentadas con emulador.
- F3: cuenta Free/modelo/cuotas efectivas, clave sólo por entorno del servidor, llamada real y revisión de calidad/tiempo de frase. La variable no definida en esta shell NO demuestra ausencia de cuenta.
- F5/F6: métrica exacta80%, fecha/hora AVA, freeze, canal privado jks y planificación/colaboración. Aprobación del criterio Groq no resuelve esos pendientes.

Fuente oficial de requisitos backend consultada: https://docs.spring.io/spring-boot/system-requirements.html (Spring Boot4.1.2, Java17+ y Maven3.6.3+). Maven instalado3.8.7/Java21 cumplen mínimos declarados; arranque real aún pendiente F1.
