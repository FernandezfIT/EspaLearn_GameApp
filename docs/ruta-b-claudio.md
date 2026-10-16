# Ruta B — Guía de trabajo de Claudio Novoa

[README](../README.md) · [Contratos K0–K6](contratos.md) · [Alcance](alcance-y-decisiones.md) · [Pruebas](pruebas.md)

## Qué debes entregar

La lógica y datos del MVP, no otra aplicación: un perfil local, cuatro unidades (Hola, Adiós, Pan, Manzana), tres etapas y recursos cámara/háptica. Felipe prepara UI/navegación. No crear pantallas paralelas, backend ni selectores extra para probar tu ruta; usa fakes y tests identificados.

**Actualización16/10:** A ya tiene UI y demo debug en el commit local `e2c8686` de `feature/base-ui`. La ampliación documental anterior está en develop59046f9; no se afirma que la rama A esté publicada o integrada. Los contratos de comportamiento siguen aprobados por Felipe; falta revisar/materializar las firmas de B. No se afirma tu aceptación de invitación, clonación, compilación ni avance sin evidencia.

## Qué tiene A y qué necesita ahora

- Pantallas Perfil/Inicio/Sesión/Resumen, carga/error, mapa, feedback, avatar geométrico/marco, swipe y navegación reutilizable. Navigation Compose2.9.7 añadido al catálogo.
- Fixtures y `NavegacionDemo` en `src/debug`; MainActivity todavía es plantilla. Las acciones de demo no guardan, admiten respuestas ni completan etapas.
- Tipos de A bajo `ui`: `EtapaMapaUi`, `SesionPresentacion`, `OpcionRespuestaUi`, `FeedbackUi`, `ResumenPresentacion`, `LogroResumenUi`. Mantener tus modelos de negocio/estado y acordar adaptación; no convertir los tipos visuales en almacenamiento por defecto.
- Prioridad de entrega: B1 compilable con ejemplos y firmas; después B2/B3 y B4 para cerrar comportamiento/persistencia. Avisar cambios de contratos/owners/factories antes de conectar. No hace falta esperar a cámara/háptica para revisar B1.
- `clavePaso` de la tarjeta es identidad visual. Debes validar sesión/paso y deduplicar botón/swipe en B2, incluso si la UI se deshabilita.
- Painter y marco dibujado no son archivo conservado ni preferencia guardada; B4/B6 siguen siendo necesarios.

Felipe cierra el16/10 y espera confirmación de tus checks para continuar A. Comparte rama/commit, pruebas reales y qué está listo/pendiente; el flujo de PR acordado no cambia. Se prioriza funcionamiento; TalkBack avanzado se aplaza salvo exigencia expresa, sin retirar básicos ni inventar dispensa general.

## Primero: entorno e integración

1. Seguir [entorno-desarrollo.md](entorno-desarrollo.md); aceptar acceso, obtener develop y ejecutar la pantalla base.
2. Leer alcance y contratos. Registrar dudas/cambios en el PR antes de alterar firmas compartidas.
3. Crear rama corta desde develop actualizada, por ejemplo feature/b-modelos.
4. Entregar primero B1: contrato compilable pequeño para desbloquear UI de Felipe.
5. PR hacia develop con revisión de Felipe antes de integrar. No modificar main directamente.

## Convención técnica inicial para B1

Los nombres siguientes son la propuesta técnica documentada de arranque, no archivos ya creados. Acordar las firmas en el primer PR y, desde entonces, no renombrar sin actualizar consumidores y avisar. Crear archivos al implementar su responsabilidad; no generar todo el árbol vacío.

| Ubicación relativa a com.example.espalearn | Tipo/responsabilidad prevista |
|---|---|
| model/Perfil.kt | Perfil confirmado: nick normalizado, edad Int y referencia de avatar. |
| model/AvatarRef.kt | Distinguir avatar predeterminado y archivo privado; no persistir bitmap ni Context. |
| model/Etapa.kt | Tres identificadores estables: DESCUBRIR, CON_APOYO, MENOS_APOYO. |
| model/Unidad.kt | ID, tema, textos español/turco, ilustración y opciones identificadas/respuesta. |
| model/Progreso.kt | Conjunto de etapas completadas, etapa activa y marco aplicado; logro derivado. |
| model/state/ | AppUiState, PerfilUiState, PerfilErrores y SesionUiState con los estados de K0–K3. |
| repository/AppRepository.kt | Entrada única para observar carga/datos, guardar perfil e imagen confirmada, iniciar/finalizar etapa y cambiar marco. |

PerfilUiState mantiene la edad del borrador como texto; Perfil confirmado usa Int. Errores de campo separados de fallo de captura o persistencia. El estado debe distinguir sin perfil de error/cargando.

El repositorio puede usar datos de prueba en tests, pero éxito de guardado significa escritura confirmada. Finalizar etapa debe ser idempotente. La firma debe devolver/comunicar fallos, no silenciarlos. Preferir operaciones de intención (finalizar etapa) a exponer escrituras sueltas para que la UI construya reglas de progreso.

**B1 terminado:** tipos y firmas compilables, estados de ejemplo válidos/error/guardando para A, PR revisado e integrado. No requiere que DataStore ya esté implementado.

## Orden de entregas y criterios

| Bloque | Trabajo | Prueba mínima y dependencia |
|---|---|---|
| B2 — Estado/sesión | ViewModels, factories y transiciones K0/K3/K4; IDs de sesión/paso para ignorar duplicados y eventos tardíos. | Fake explícito: Descubrir sin elección, una respuesta, feedback, Continuar manual; no marcar finalización antes de persistir. Depende de B1 y contenido de prueba. |
| B3 — Validación | PerfilValidator centralizado; nick/edad y errores al Guardar, luego reactivos al corregir. | Límites y caracteres; no depender de Compose. Puede avanzar en paralelo a B2. |
| B4 — Datos duraderos | LocalAppRepository y AppPreferencesStore; carga, perfil/progreso/marco, errores, AppContainer. | Reapertura, preservación de progreso al editar, repetición sin duplicados y fallo de escritura. Repositorio comprobable antes de UI final. |
| B5 — Háptica | Adaptador AnswerHaptics en frontera Android, señal sólo por elección admitida. | Un pulso neutral; mismo efecto por botón/swipe; sin duplicación/Continuar/restauración; probar físicamente. |
| B6 — Cámara | AvatarFileStore + AvatarCapture, launcher, URI/FileProvider, candidata, confirmación, recuperación/limpieza. | Capturar/cancelar/fallar, edición descartada y recuperación de imagen guardada. A conecta botón/preview. |
| B7 — Robustez | Rotación, proceso, fallos de E/S, archivos ausentes, captura pendiente. | Casos y resultados reales en pruebas.md. |
| B8/B9 — Cierre | Tests, matriz física, correcciones y documentación técnica de implementación. | Copia limpia y pruebas disponibles; no añadir frameworks al cierre sin necesidad. |

## Estado y ciclo de vida

- ViewModels exponen StateFlow de lectura; la UI representa estado y emite acciones.
- Routes conectan ViewModel/lifecycle con Screens que reciben estado/callbacks.
- Propuesta de owners: AppViewModel compartido a nivel raíz/Activity; PerfilViewModel del destino Perfil; SesionViewModel del destino Sesión. Verificar owner/key reales en la implementación para no obtener dos instancias del estado global.
- ViewModel no guarda Activity, NavController ni launcher de cámara. Conexión manual y factories, sin Hilt.
- Recolección con collectAsStateWithLifecycle cuando se añada una dependencia compatible. No copiar versiones de otra app.
- Perfil: bloquear Atrás y envíos repetidos mientras se confirma escritura. Eso no vuelve atómica la combinación archivo+preferencias ni impide muerte del proceso.

## Validación del nick y edad

- Normalizar nick con NFC; contar puntos de código, no unidades Char; 3–20 inclusive.
- Cada punto de código: Character.isLetter(int) o ASCII 0–9. Sin trim automático; sólo numérico permitido. Marcas separadas restantes se rechazan; no prometer soporte universal de nombres.
- Comprobar letras españolas/turcas y formas equivalentes; contrastar casos representativos en Android y JVM de tests.
- Edad del borrador: parseo seguro de entero y rango 7–10 centralizado. El rango incluye 10 por decisión provisional de Felipe; confirmación docente pendiente.
- Primer Guardar activa errores; corregir después actualiza resultados. Mensaje por campo y mensajes de operación separados.

## Persistencia mínima

- Una instancia activa de Preferences DataStore por archivo, creada/reutilizada desde el contenedor, no desde cada pantalla.
- Propuesta de claves estables: versión de esquema; nick; edad; referencia de avatar; etapas completadas; etapa activa; marco aplicado. Acordar nombres/serialización y manejo de valores desconocidos en B4.
- Guardar campos relacionados en una actualización coherente de preferencias. No persistir estados transitorios de feedback, puntuación o historial.
- No transformar un error de lectura en perfil vacío ni confirmar éxito si una escritura falla. Preservar datos al editar o repetir.

## Cámara y archivo privado

- Preferencia: captura delegada mediante ActivityResultContracts.TakePicture y URI de FileProvider con rutas restringidas, no acceso directo a cámara ni permisos generalistas copiados por reflejo.
- Foto nueva a archivo candidato privado; sólo cambia el perfil al confirmar guardado válido. Conservar archivo/referencia anterior hasta éxito.
- Protocolo a concretar en B6: preparar/verificar archivo, persistir referencia, retirar anterior después del éxito y limpiar huérfanos de forma segura. No es una transacción única entre archivo y DataStore.
- Preservar referencia pendiente cuando corresponda para recibir resultado tras recreación; probar retorno de cámara y muerte de proceso, no sólo rotación.
- Cancelar no es error; fallo tiene mensaje/reintento. Falta de archivo al abrir usa avatar predeterminado y tratamiento recuperable, sin borrar perfil/progreso.
- Revisar manifest combinado y permisos según contrato real; volver a contrastar APIs vigentes al implementar. Ambos disponen de teléfono; registrar pruebas.

## Háptica

Usar un adaptador de feedback de interacción, preferentemente View.performHapticFeedback con constante apropiada y sin eludir ajustes. Esa API no requiere VIBRATE; si se elige otra basada en Vibrator, revisar permisos y justificarla. No disparar por renderizado; identidad/consumo de señal deben impedir repetición por restauración. Feedback visual independiente del hardware.

## Cada PR debe indicar

- Bloque B y objetivo, archivos y contratos afectados.
- Qué es fake y qué está integrado con datos reales.
- Pruebas ejecutadas y resultados; si no se ejecutaron, indicarlo.
- Cambios requeridos de A y actualización del checklist.
- Revisión de Felipe antes de merge; resolver develop roto antes de continuar.

## Qué no está validado

Compatibilidad completa del conjunto de versiones, dispositivo de Claudio, traducciones, licencias finales, pedagogía y recursos físicos. La consulta educativa del caso no se realizó y Felipe decidió priorizar el aspecto técnico de EV2; no inventar esa evidencia. Ver [criterios-pedagogicos.md](criterios-pedagogicos.md).

Referencias técnicas: [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore), [FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider), [TakePicture](https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContracts.TakePicture), [háptica](https://developer.android.com/develop/ui/views/haptics/haptic-feedback). Algunas verificaciones se apoyaron en código oficial AndroidX; reevaluar dependencias y manifest concretos al implementar, no asumir garantías por el documento.
