# EspaLearn_GameApp

Aplicación Android educativa de demostración para el acercamiento inicial al español de niños y niñas turcohablantes. Proyecto académico de **Desarrollo de Aplicaciones Móviles (DSY1105), EV2**.

> El recorrido técnico está implementado y probado en el entorno indicado abajo. No se afirma validación lingüística/pedagógica, revisión del docente ni entrega académica completada. La consulta educativa del caso no se realizó; la propuesta de edad simulada 7–10 difiere del público escrito «menor de 10». Ver [alcance](docs/alcance-y-decisiones.md) y [matriz EV2](docs/matriz-ev2.md).

## Integrantes y aportes

| Integrante | Ruta | Aportes |
|---|---|---|
| Felipe Fernández | A — Interfaz y experiencia | Base del proyecto, pantallas/componentes Compose, navegación inicial, gestos, adaptabilidad y cierre integrado con asistencia de IA. Ejecutó compilación, suites y comprobaciones físicas en Xiaomi. |
| Claudio Novoa | B — Lógica y datos | Modelos, validador, ViewModels, repositorio/DataStore, plataforma de cámara/háptica, catálogo, conexión inicial de pantallas y pruebas de lógica. |

Ambos participan en integración, revisión y documentación. La existencia de cambios o de este README no sustituye una revisión del otro integrante.

## Estado actual

**Cierre local: `feature/cierre-ev2`, sobre `develop` en `427f1ef` (merge del PR #2 de lógica/datos).** La base ya incluye la UI de `feature/base-ui`. Los cambios del cierre aún no tienen commit ni publicación remota; no se afirma un PR nuevo ni entrega.

- `Run → app` abre el flujo real, no `Hello Android` ni una demo prefijada.
- Perfil único, validación centralizada, borrador restaurable y avatar por cámara conectados a datos reales.
- Cuatro unidades con dibujos propios Canvas: **Hola, Adiós, Pan, Manzana**.
- Tres etapas: **Descubrir → Con apoyo → Menos apoyo**; 12 pasos en total, 8 elecciones.
- Botones y swipe comparten respuesta, bloqueo de duplicados y pulso háptico neutral; feedback y avance manual.
- Progreso, avatar y marco opcional guardados localmente.
- Resumen automático sólo en el primer cierre confirmado de etapa 3. Etapas 1/2 y repasos vuelven a Inicio. **Ver resumen** aparece debajo de etapa 3 al completar el módulo.
- `EspaLearnApplication` conserva un contenedor/repositorio por proceso, también al recrear la Activity.

### Evidencia de esta versión local

| Comprobación | Resultado |
|---|---|
| `:app:assembleDebug` | Compilación correcta, ejecutada por Felipe. |
| `:app:testDebugUnitTest` | **99 pruebas, 0 fallos y 0 errores**; XML inspeccionados. |
| `:app:connectedDebugAndroidTest` | **6 pruebas, 0 fallos y 0 errores**; XML inspeccionados. |
| `:app:lintDebug` | **0 errores, 16 advertencias**; informes inspeccionados. |
| Pruebas manuales | Felipe confirmó perfil/cámara/cancelación/reapertura, dibujo/elección/háptica, rotación, navegación de repaso y conservación de nick/progreso/marco en Xiaomi. |

Dispositivo identificado por el runner como `25069PTEBG - 16`, Xiaomi según Felipe. Las instrumentadas cubren identidad del contenedor tras recreación, disponibilidad/posición/acción de Ver resumen, apertura desde Inicio ante escritura pendiente/fallida y contexto de plantilla. Las pruebas de Inicio usan la Route real con escritura sustituida y verifican que sólo se solicite navegación tras éxito; no fuerzan un fallo nativo de DataStore ni son una suite completa de cámara/navegación.

Resultados, límites y secuencia de cambios en [pruebas.md](docs/pruebas.md). No se atribuye ejecución desde una copia limpia ni compatibilidad física comprobada en API 33.

## Flujo implementado

1. **Arranque:** carga el perfil/progreso; formulario si no existe perfil, Inicio si existe, reintento ante error de lectura.
2. **Perfil:** nick ficticio NFC de 3–20 puntos de código, letras Unicode o dígitos ASCII; edad simulada 7–10. Errores por campo al Guardar y luego al corregir, con mensaje e icono. Guardar bloquea la salida mientras escribe; Atrás descarta el borrador.
3. **Avatar:** cámara delegada mediante `TakePicture` y FileProvider privado; sólo ilustraciones autorizadas. Foto candidata visible, confirmada junto con un perfil válido; cancelar otro intento conserva la anterior. Lectura de miniatura fuera del hilo UI; archivo ausente/ilegible usa avatar geométrico.
4. **Inicio:** mapa secuencial de tres etapas, edición del perfil y, con módulo completo, botón Ver resumen. Abrir una etapa espera el registro confirmado, bloquea acciones mientras escribe y conserva Inicio con mensaje/reintento ante fallo.
5. **Descubrir:** dibujo, español y apoyo turco, sin pregunta. **Con apoyo:** dibujo/turco y dos opciones españolas. **Menos apoyo:** dibujo/opciones y turco al pulsar Ver ayuda. La respuesta española se revela después de elegir en práctica.
6. **Finalización:** sólo tras escritura confirmada. No exige nota mínima ni duplica etapas. Resumen final automático únicamente al completar por primera vez etapa 3; los repasos vuelven a Inicio.
7. **Resumen:** logro, aplicar/quitar marco, Repasar desde Descubrir sin borrar progreso y Volver a Inicio. Repasar espera el registro de etapa antes de navegar.

### Reanudación y límites

- Los datos confirmados se conservan localmente; el borrador del formulario usa `SavedStateHandle` y el URI de captura pendiente usa estado guardado de Compose.
- Una sesión que Android conserva puede continuar en el paso actual. Una nueva instancia de sesión empieza la etapa desde el paso 1; **no se persiste el índice de tarjeta**.
- En la prueba de reapertura, Felipe observó continuidad en paso 2 y la aceptó. No se verificó el PID antes/después de `am kill`, por lo que no se acredita muerte real del proceso ni recuperación exacta de tarjeta tras ella.
- Fallos nativos de E/S/cámara, ajustes hápticos, API mínima y primera celebración con datos iniciales vacíos aún requieren ampliar comprobación física. Los fakes no sustituyen esos casos.
- Traducciones y distractores siguen como propuesta no validada. Las cuatro ilustraciones son propias, creadas con asistencia de IA, no imágenes externas. Falta registrar la procedencia de la ilustración física usada en cámara.
- Sin login, backend, cuentas múltiples, audio, puntuaciones, historial de respuestas, vidas, tienda ni selector de temas. No se añaden funciones de EV3 al cierre de EV2.

## Entorno y ejecución

| Configuración | Valor del proyecto |
|---|---|
| Módulo / application ID | `app` / `com.example.espalearn` |
| SDK mínimo / compilación / objetivo | 33 / 37 / 37 |
| Android Gradle Plugin / Wrapper | 9.3.3 / 9.5.0 |
| Kotlin Compose / Compose BOM | 2.2.10 / 2026.02.01 |
| Navigation Compose | 2.9.7 |
| DataStore / coroutines-test | 1.2.1 / 1.10.2 |
| Compatibilidad de fuentes/bytecode Java | 11 |
| Criterio JVM del daemon | 25; no confundir con compatibilidad Java 11. |

1. Obtener el [repositorio](https://github.com/FernandezfIT/EspaLearn_GameApp) y seleccionar la rama que contenga el cierre. `develop` inspeccionado es sólo la base hasta publicar/integrar estos cambios.
2. Abrir la carpeta con `settings.gradle.kts` en Android Studio, configurar SDK/JVM y sincronizar con Gradle Wrapper.
3. Seleccionar `app` y un dispositivo compatible; ejecutar **Run**.
4. Usar datos ficticios y fotografiar únicamente una ilustración autorizada.

En PowerShell, desde la raíz:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Con dispositivo/emulador conectado:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

En Bash, usar `./gradlew`. Informes locales: `app/build/reports/tests/testDebugUnitTest/`, `app/build/reports/androidTests/connected/` y `app/build/reports/lint-results-debug.*`. No versionar informes, APK, estado del IDE, SDK local ni credenciales. Para el entorno completo, ver [guía](docs/entorno-desarrollo.md).

Las previews y `NavegacionDemo` siguen en `src/debug` como escenarios sintéticos aislados; no son la entrada del producto ni evidencia de persistencia.

## Organización del código

Ubicación: `app/src/main/java/com/example/espalearn/`.

```text
EspaLearnApplication.kt   # contenedor único por proceso
MainActivity.kt          # tema, Scaffold y NavHost real
app/                     # conexión manual de dependencias
domain/                  # PerfilValidator
model/ y model/state/    # dominio y estados
repository/              # contrato, DataStore, repositorio local y fake
viewmodel/               # App, Perfil, Sesión y factories
platform/                # cámara, archivos privados y háptica
navigation/              # destinos y conexión de acciones/estado
ui/
  components/            # formulario, avatar, dibujos, tarjetas, mapa y feedback
  mapping/               # proyecciones de presentación
  screens/               # arranque, perfil, inicio, sesión y resumen
  theme/
```

Unitarias en `src/test`; instrumentadas en `src/androidTest`; previews en `src/debug`. Las pantallas representan estado y emiten acciones; validación y persistencia están fuera de los composables.

## Documentación y pendientes de entrega

- [Checklist](docs/tareas-y-dependencias.md): corte vigente y planificación histórica.
- [Contratos](docs/contratos.md): reglas K0–K6 y navegación vigente.
- [Pruebas](docs/pruebas.md): evidencia, límites y resultados comunicados.
- [Matriz EV2](docs/matriz-ev2.md): indicadores y pendientes, sin porcentajes de cumplimiento ficticios.
- [Contenido y licencias](docs/contenido-y-licencias.md): catálogo, dibujos y revisión pendiente.
- [Alcance](docs/alcance-y-decisiones.md), [criterios pedagógicos](docs/criterios-pedagogicos.md), [ruta B](docs/ruta-b-claudio.md) y [handoff B](docs/entrega-ruta-logica-datos.md).

Pendientes: límites de pruebas arriba indicados, revisión lingüística/académica, revisión de Claudio del cierre, publicación/PR, prueba desde copia limpia y confirmación de entrega/fechas en AVA. No hay entrega ni congelamiento declarado.

El seguimiento Markdown reemplaza el tablero externo según indicación docente comunicada por Felipe; no elimina la evidencia de participación real de ambos.

## Privacidad

Usar nick y edad simulados; no fotos, voces ni identificadores de menores. No afirmar entrevistas, permisos, aprobación docente o resultados sin evidencia. La cámara se limita a ilustraciones autorizadas, nunca a selfies de menores.
