# F0 — verificación de base y contratos

Fecha: 18/11/2026. Estado: **base y contratos de dominio listos para iniciar F1**, con verificaciones de integración explícitamente diferidas abajo. No significa funcionalidad EV3 terminada ni entrega evaluada.

## Base y entorno

- Proyecto Mint `/home/fernandezf-it/Escritorio/EspaLearn_turco_espaniol`, rama preparada previamente `feature/Experiencia_integracion` desde develop `d83b480`. En F0 no se ejecutaron comandos Git, commit ni push.
- Android: AGP9.3.3, Wrapper9.5.0, Kotlin Compose plugin2.2.10, Compose BOM2026.02.01, min33/compile37/target37, Java bytecode11.
- Gradle informa Kotlin embebido2.3.20: no confundirlo con el plugin Compose2.2.10 del catálogo.
- LauncherJava21.0.13; daemon Gradle comprobado ejecutándose desde toolchain Adoptium25. No se modificaron versiones.
- Backend acordado con Felipe: Maven+Java21, separado del build Android. Maven3.8.7 instalado; parent Spring Boot4.1.2 respondió HTTP200 y requisitos oficiales permiten Java21/Maven3.8.7. Arranque real en F1.
- Docker29.10.0 y Composev5.1.3 instalados. Contexto habitual desktop-linux no responde; `docker --context default info` responde29.10.0. Usar `docker --context default compose ...` en F1. No se cambió contexto global ni se arrancó MySQL.
- Emulador elegido por Felipe. AVDPixel_6a arrancado sin ventana; runner lo identifica Android17, API37 corroborada con adb. Backend previsto en10.0.2.2:8080, sin modificar red Android en F0. Se detuvo el emulador iniciado por el asistente al terminar las comprobaciones.

## Comprobaciones ejecutadas

Desde raíz:

```bash
./gradlew --version
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
./gradlew :app:connectedDebugAndroidTest --console=plain
```

| Comprobación | Resultado comprobado |
|---|---|
| Baseline EV2 antes del código nuevo | Build correcto,99 unitarias/0fallos/0errores; lint0errores/18advertencias |
| Tras contratos F0 | Build correcto,114 unitarias/0fallos/0errores/0omitidas |
| Instrumentadas existentes | 6/0fallos/0errores/0omitidas en Pixel_6a API37 |
| Lint final | 0errores/18advertencias; no afirmar los16 históricos |
| Fixtures JSON | Parseo y comprobaciones básicas de lote, orden, Descubrir y longitud correctos; serialización HTTP aún no probada |

Reportes XML/HTML inspeccionados en:

- `app/build/test-results/testDebugUnitTest/` y `app/build/reports/tests/testDebugUnitTest/`
- `app/build/outputs/androidTest-results/connected/debug/` y `app/build/reports/androidTests/connected/`
- `app/build/reports/lint-results-debug.xml` / `.html`
- APKdebug: `app/build/outputs/apk/debug/app-debug.apk` (aprox31MiB).

Logs auxiliares de esta ejecución en `/tmp/espalearn-f0-*.log`, temporales y no parte del repositorio. Los informes de build pueden sobrescribirse al repetir tareas; no son evidencia ligada a un nuevo commit.

## Archivos añadidos/cambiados

- `app/src/main/java/com/example/espalearn/domain/ev3/ContratosEv3.kt`: tipos, validaciones, puertos suspend y función pura de recompensa legada, aislados del runtime EV2.
- `app/src/test/java/com/example/espalearn/domain/ev3/ContratosEv3Test.kt`:15 pruebas nuevas de lote, orden, catálogo/opciones, resultados, versiones, estados de feedback, cobertura y legado.
- `docs/contratos-ev3-f0.md`: decisiones, endpoints/errores/serialización y fronteras de integración.
- `docs/fixtures/ev3-f0.json`: ejemplos sintéticos de mensajes; no contenido editorial certificado ni salida Groq real.
- `.gitignore`: exclusiones preventivas de env/llaves/keystore.properties, permitiendo `.env.example`. No sustituye auditoría de secretos ya rastreados.
- README: acceso a documentación vigente y advertencia de que el bloque EV2 es histórico.

No se cambió navegación, ViewModels existentes, DataStore ni comportamiento de juego. La migración sólo está modelada/probada como función pura; aún no escribe preferencias.

## Decisiones de Felipe en F0

1. Conservar marco legado EV2, independiente de cobertura/primer recorrido EV3; también si estaba desactivado.
2. Emulador como dispositivo principal para desarrollo.
3. Maven+Java21 para backend.

## Límites / puertas de siguientes bloques

- Sin backend/BD/Room/Retrofit/Groq implementados. Ninguna llamada real de IA; GROQ_API_KEY/GROQ_MODEL no estaban definidas en esta shell. No se infiere si existe cuenta ni se pide compartir claves en chat.
- Cuenta Free, modelo, límites reales y calidad/salida deben comprobarse en F3 antes de declarar EX01 cumplido.
- Compatibilidad/resolución Room/KSP/Retrofit y herramienta de cobertura se ensayan en F2; todavía no hay dependencias candidatas instaladas. No se declara cerrado ese subcontrol de toolchain.
- MySQL8.4 propuesto: descargar/fijar imagen y probar persistencia/transacciones en F1. No hay contenedor ni volumen creado aquí.
- Pruebas nuevas son unitarias de contrato, no tests de idempotencia SQL, concurrencia, cache ni transporte. Fixtures Kotlin/JSON deben contrastarse con el serializador real futuro.
- Sin cobertura medida ni cumplimiento80% acreditado. Las seis instrumentadas heredadas no cubren todavía integración EV3, cámara física, red/permisos LAN ni release.
- APKgenerada es debug, no release firmada de entrega. No se atribuye ensayo visual manual, aceptación docente final, validación pedagógica ni colaboración nueva de Claudio.

**Siguiente paso F1:** crear backend independiente Maven/Java21, MySQL mediante contexto default, catálogo fixture3 y endpoints iniciales crear/consultar, con tests y reinicio persistente. No comenzar por estética ni por150archivos vacíos.
