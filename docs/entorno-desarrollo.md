# Entorno de desarrollo

[Volver al README](../README.md) · [Checklist de avance](tareas-y-dependencias.md)

**Actualización:** 15/10/2026. Objetivo: permitir que ambos integrantes obtengan, ejecuten y verifiquen la misma base sin compartir rutas personales ni estado del IDE.

## 1. Base compartida

- Repositorio: <https://github.com/FernandezfIT/EspaLearn_GameApp>.
- Nombre del repositorio: `EspaLearn_GameApp`.
- Nombre local original del proyecto: `EspaLearn_turco_espaniol`; no necesita coincidir con la carpeta creada al clonar.
- `main`: base estable. `develop`: integración. Ambas publicadas según confirmación de Felipe.
- Ramas cortas `feature/...` desde develop por tarea; PR hacia develop con revisión del otro integrante antes de integrar. main recibe versión estable revisada, según acuerdo con Felipe.
- Invitaciones enviadas a Claudio y docente; aceptación aún pendiente de comprobación.
- Seguimiento: [checklist Markdown](tareas-y-dependencias.md), en reemplazo de Trello por indicación docente comunicada por Felipe.

## 2. Configuración observada

| Elemento | Valor | Fuente |
|---|---|---|
| Módulo | `app` | `settings.gradle.kts` |
| Namespace / application ID | `com.example.espalearn` | `app/build.gradle.kts` |
| SDK mínimo | 33 | `app/build.gradle.kts` |
| SDK de compilación | 37 | `app/build.gradle.kts` |
| SDK objetivo | 37 | `app/build.gradle.kts` |
| Android Gradle Plugin | `9.3.3` | `gradle/libs.versions.toml` |
| Gradle Wrapper | `9.5.0` | `gradle/wrapper/gradle-wrapper.properties` |
| Plugin Kotlin Compose | `2.2.10` | `gradle/libs.versions.toml` |
| Compose BOM | `2026.02.01` | `gradle/libs.versions.toml` |
| Compatibilidad de fuentes y bytecode Java | 11 | `app/build.gradle.kts` |
| Criterio de JVM del daemon | 25 | `gradle/gradle-daemon-jvm.properties` y captura de Android Studio |

Esta tabla registra valores del proyecto, no una certificación de compatibilidad de todas sus versiones. No actualizar dependencias sólo por existir una versión más reciente.

El mínimo SDK 33 no implica compilar ni ejecutar exclusivamente en API 33. Ejecutar la pantalla base en API 37.1 tampoco demuestra que todas las funciones operen en API 33.

**Java 11 y JVM 25 son ajustes diferentes:** la compatibilidad de fuentes/bytecode Java no identifica el JDK que ejecuta Gradle. El criterio del daemon selecciona una JVM que cumpla sus requisitos; falta registrar la ruta/version efectiva utilizada en cada equipo. El Java mostrado por la terminal no debe tomarse automáticamente como JVM efectiva del daemon.

## 3. Información conocida por integrante

| Dato | Felipe Fernández | Claudio Novoa |
|---|---|---|
| Sistema operativo | Entorno Linux observado; distribución pendiente de registrar | Pendiente |
| Android Studio | Versión exacta pendiente de registrar desde About | Pendiente |
| SDK instalado | Ejecución base reportada; inventario pendiente | Pendiente |
| JVM efectiva de Gradle | Pendiente de registrar; criterio 25 configurado | Pendiente |
| Dispositivo de prueba | Pixel 6a emulado, Android 17 / API 37.1, según Felipe | Pendiente |
| Pantalla base ejecutada | Sí, confirmado por Felipe | Pendiente |
| Dispositivo físico para recursos | Disponible según Felipe; modelo/API por registrar | Disponible según Felipe; modelo/API por registrar |

No copiar rutas absolutas de un equipo al otro. Completar esta tabla con comprobaciones reales.

## 4. Primera ejecución de Claudio

Pasos a realizar por Claudio; no se consideran ejecutados todavía.

1. Aceptar la invitación al repositorio y comprobar el acceso.
2. Elegir una ubicación local y obtener el proyecto:

   ```bash
   git clone https://github.com/FernandezfIT/EspaLearn_GameApp.git
   cd EspaLearn_GameApp
   git switch develop
   ```

3. Abrir en Android Studio la carpeta que contiene `settings.gradle.kts`.
4. Revisar la instalación del SDK requerido y la configuración de Gradle. Mantener **Distribution: Wrapper** y revisar el criterio JVM definido por el proyecto. Si falta un requisito, registrar el mensaje antes de cambiar versiones.
5. Ejecutar **Sync Project with Gradle Files** y esperar el resultado.
6. Seleccionar `app` y un emulador/dispositivo adecuado; ejecutar con **Run**.
7. Verificar que aparece la pantalla base y registrar equipo, dispositivo y resultado en este documento.
8. Comprobar que la configuración local no introduce cambios inesperados en archivos compartidos antes de preparar commits.

**Resultado esperado actual:** pantalla de ejemplo Compose, no el MVP educativo.

Si falla: registrar el primer error de sincronización o compilación con sus detalles y el paso que lo produjo. No borrar cachés, añadir dependencias o cambiar SDK/JDK a ciegas.

## 5. Qué se comparte y qué permanece local

### Compartido en Git

- Código y recursos autorizados del proyecto.
- Scripts Gradle y catálogo de versiones.
- Wrapper completo: `gradlew`, `gradlew.bat`, JAR y propiedades.
- `gradle/gradle-daemon-jvm.properties` con criterios de JVM.
- `.gitignore`, README y documentación.

### Excluido

- `.idea/`: configuración local del IDE, por decisión inicial del proyecto.
- `.gradle/`, `.kotlin/` y directorios de build.
- `local.properties`: ubicación local del SDK.
- Credenciales, tokens y claves privadas/de firma; no incorporarlos aunque una regla concreta de exclusión aún no exista.

`gradle/` no es `.gradle/`: el primero contiene configuración compartida necesaria; el segundo, datos generados.

**Estilo e inspecciones:** no se ha creado `.editorconfig` ni una configuración IDE compartida. Si se acuerdan reglas automáticas, se añadirán explícitamente; no afirmar que ambos editores están homologados sólo por compartir el build.

## 6. Comprobaciones pendientes

Estas comprobaciones son propuestas para ejecutar cuando se acuerde, no resultados obtenidos en esta sesión:

| Comprobación | Propósito | Estado |
|---|---|---|
| `./gradlew --version` | Registrar información del Wrapper/JVM que reporte Gradle; contrastar la JVM del daemon cuando proceda. | Pendiente |
| `./gradlew :app:assembleDebug` | Comprobar compilación debug mediante Wrapper. | Pendiente |
| `./gradlew :app:testDebugUnitTest` | Ejecutar pruebas unitarias disponibles. | Pendiente |
| `./gradlew :app:lintDebug` | Ejecutar análisis Android de la variante debug. | Pendiente |
| Ejecución por Claudio desde copia propia | Comprobar reproducibilidad entre integrantes. | Pendiente |
| Ejecución en API 33 | Comprobar la base en el mínimo declarado. | Pendiente |
| Pruebas físicas de recursos | Comprobar integración real cuando se implemente y apruebe. | Pendiente |

En Windows utilizar `gradlew.bat` en lugar de `./gradlew`. Las tareas de build/prueba pueden generar archivos locales y descargar dependencias; no se han ejecutado automáticamente al redactar esta guía.

## 7. Evidencia actual y siguiente cierre

- Felipe informó ejecución de la aplicación base en emulador, primer commit, publicación de `main`/`develop` e invitaciones enviadas.
- Se revisaron configuración local, exclusiones y comprobación de whitespace del primer commit; esta última terminó sin mensajes según Felipe.
- No se ha verificado ejecución por Claudio, pruebas automatizadas ni recursos físicos.
- K0–K6 y alcance mínimo publicados en develop en commit 1ce1867. La ampliación documental de cierre posterior está local y pendiente de revisión/publicación; no se ejecuta Git automáticamente al redactarla.

**Cierre del entorno compartido:** ambos integrantes ejecutan la misma base y registran sus resultados. Esto no cierra por sí solo E0: también faltan los contratos y acuerdos indicados en el checklist.

## Referencias

- Material EA2: guía 7, configuración inicial y colaboración; guía 11, referencia API 33 o superior. La frase de la guía no fija por sí sola los tres parámetros SDK.
- [Android: estructura del build](https://developer.android.com/build/android-build-structure).
- [Android: versiones Java y JDK](https://developer.android.com/build/jdks).
- [Gradle: Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).
- [Gradle: criterios de JVM del daemon](https://docs.gradle.org/current/userguide/gradle_daemon.html#sec:daemon_jvm_criteria).
