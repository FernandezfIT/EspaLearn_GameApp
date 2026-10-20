# Plan y registro de pruebas

[README](../README.md) · [Contratos](contratos.md) · [Ruta B](ruta-b-claudio.md)

## Estado real

**Actualización:16/10/2026.** Felipe comunicó compilación debug y comprobaciones manuales de UI/demo. El asistente inspeccionó archivos, pero no ejecutó build, lint ni suite. Los casos de integración siguientes continúan pendientes o sólo tienen cobertura visual parcial; una preview no acredita persistencia, validación real, recursos físicos ni aprendizaje.

Base de cierre: `feature/base-ui`, commit local `e2c8686`, que consolida el código incorporado durante la sesión. No implica que cada comprobación se haya corrido desde ese commit ya consolidado o desde una copia limpia.

Entorno comunicado: Windows, emulador Pixel6a Android17/API37.1 y ejecución real adicional del swipe reportada sin modelo/API. Ambos disponen de teléfono físico, pero no hay prueba de cámara/háptica registrada. No usar datos, fotos ni voces reales de menores.

## Registro manual comunicado — 16/10

Los resultados son confirmaciones de Felipe durante la construcción guiada, no asserts automatizados ni observaciones independientes del asistente.

| Registro | Escenario y alcance | Resultado comunicado | Límite |
|---|---|---|---|
| R01 | CampoPerfil y Perfil: edición local, errores prefijados, guardando y callbacks de demo. | Funcionamiento OK. | Sin validator, escritura ni bloqueo real de Atrás. |
| R02 | Tarjeta: Descubrir/Con apoyo/Menos apoyo y Ver ayuda inline. | Ayuda funciona; deformación de Preview no se reprodujo en emulador. | No se confirmó causa del renderizado; no hay traducción/ilustración final. |
| R03 | TarjetaDeslizable aislada e integrada: arrastre, retorno, cancelación por desplazamiento insuficiente y equivalencia A/B con botones. | Funcionamiento OK; swipe reportado en «equipo real». | Identidad/deduplicación B y modelo/API de ese equipo pendientes. |
| R04 | Sesión y Resumen: snapshots iniciales/feedback/guardando, logro y solicitud de aplicar/quitar marco. | Previews OK. | No transición real ni finalización o marco persistido. |
| R05 | NavegacionDemo: Inicio–Perfil y Atrás, sesión de prueba, Resumen explícito y retorno sin dejarlo detrás. | Todo funciona según Felipe. | Raíz fija Inicio; MainActivity, creación/guardado y estado B no conectados. |
| R06 | Avatar geométrico en Perfil/Inicio/Resumen, con/sin marco. | Todo bien tras corregir ubicación dentro del logro no nulo. | Rama Painter con imagen autorizada/cámara pendiente. |
| R07 | Inicio una/dos columnas, tamaños compactos/amplios/horizontales y fuente ampliada; indicación de comprobar teclado. | Funcionamiento general confirmado. | Sin capturas/log de cada combinación ni configuración exacta del equipo; verificar de nuevo con el flujo real. |
| R08 | Compilación tras añadir Navigation2.9.7; se indicó assembleDebug por Wrapper en Windows. | Compila sin problemas según Felipe. | Sin salida de consola adjunta; no se ejecutó desde el asistente ni acredita tests/lint. |

Carga/error de arranque tienen previews creadas, pero no una lectura real ni un resultado individual de prueba claramente registrado. **TalkBack no se probó**: aunque Felipe indicó disponer de teléfono para ello, después decidió aplazar accesibilidad avanzada salvo obligación docente expresa. No convertir esa disponibilidad en una prueba superada.

Al retomar, completar modelo/API, versión de app/commit exacto y evidencia cuando se ejecute cada caso integrado.

## Casos mínimos

| ID | Caso y resultado esperado | Responsable principal | Tipo | Resultado |
|---|---|---|---|---|
| P01 | Cargando no muestra formulario prematuramente; lectura sin/con perfil entra al destino correcto. | B + A | Integración K0 | Pendiente |
| P02 | Error de lectura muestra Reintentar sin convertir datos existentes en perfil vacío. | B | Fake de fallo + integración | Pendiente |
| P03 | Nick NFC de 3/20 puntos de código aceptado; 2/21, vacío, espacios y símbolos rechazados. | B | Unitario | Pendiente |
| P04 | Letras españolas/turcas y formas equivalentes; sólo dígitos ASCII admitidos y nick numérico permitido; marcas separadas rechazadas. | B | Unitario + casos Android | Pendiente |
| P05 | Edad 7/10 aceptada como propuesta; 6/11, vacío, texto y fracción rechazados. | B | Unitario | Pendiente |
| P06 | Sin errores al abrir; Guardar inválido muestra mensajes/indicadores por campo; correcciones posteriores actualizan errores. | A + B | UI/integración | Parcial: presentación R01; validación/reactividad e icono específico pendientes. |
| P07 | Guardando bloquea doble envío y Atrás; fallo conserva borrador y habilita reintento; éxito confirmado precede navegación. | B + A | Integración con fallo | Parcial: controles deshabilitados en snapshot R01; Atrás y escritura real pendientes. |
| P08 | Atrás creando descarta y sale; editando vuelve a Inicio conservando perfil/progreso previos. | A + B | UI/datos | Parcial: edición/Atrás con fixture R05; datos reales y creación pendientes. |
| P09 | Cuatro unidades, dos temas y 12 pasos: Descubrir sin elección, ocho elecciones en práctica y ayudas acordadas. | A + B | Recorrido | Parcial: configuraciones/ayuda R02/R04; catálogo y recorrido12pasos pendientes. |
| P10 | Botón/swipe tienen mismo efecto; duplicado o evento de otra sesión/paso no registra otra elección. | B + A | Unitario/UI | Parcial: mismo ID en callbacks R03; admisión única y eventos tardíos pendientes B2. |
| P11 | Feedback espera Continuar; no timer ni avance automático; error de respuesta no impide completar. | A + B | UI | Parcial: snapshot R04; transiciones y completar sin nota pendientes. |
| P12 | Acceso secuencial; finalizar espera escritura; fallo permite reintentar sin éxito ficticio. | B + A | Datos/recorrido | Pendiente |
| P13 | Reapertura conserva etapas completas y reinicia interrumpida; repetición no duplica progreso/logro. | B | Integración persistencia | Pendiente |
| P14 | Logro sólo al completar módulo; marco opcional persiste al reabrir; editar perfil conserva avance. | B + A | Integración | Parcial: presentación R04/R06; elegibilidad y persistencia pendientes. |
| P15 | Cámara produce candidata; perfil no cambia hasta Guardar válido; imagen guardada recuperable al reabrir. | B + A | Dispositivo/archivo | Pendiente |
| P16 | Cancelar cámara no es error; fallo muestra reintento; ambos conservan avatar/borrador. Atrás descarta candidata. | B + A | Dispositivo/archivo | Pendiente |
| P17 | Recreación durante captura, archivo ausente y huérfanos se recuperan/limpian sin borrar referencia vigente ni progreso. | B | Integración/robustez | Pendiente |
| P18 | Un pulso neutral por elección admitida, igual acierto/error y botón/swipe; ninguno por Continuar/duplicados/restauración. | B + A | Teléfono físico | Pendiente |
| P19 | Recorrido comprensible sin vibración disponible y respetando ajustes del dispositivo. | B + A | Teléfono físico | Pendiente |
| P20 | Rotación y muerte de proceso se prueban por separado; datos confirmados permanecen y se aplica política de reanudación. | B + A | Android | Pendiente |
| P21 | Texto ampliado, teclado, ventanas compacta/ampliada, foco/etiquetas y botones alternativos sin acciones ocultas. | A | UI/adaptabilidad básica | Parcial R03/R07; comprobación integrada pendiente. TalkBack avanzado aplazado, no probado. |
| P22 | Claudio sincroniza, compila y ejecuta desde su copia; probar mínimo API33 además del emulador de Felipe. | Ambos | Reproducibilidad | Pendiente |

## Herramientas y límites

- Base local existente: JUnit4 para unitarias y configuración de tests instrumentados/Compose generada. No afirmar que se ejecutó la suite.
- Añadir coroutines-test u otra dependencia sólo si hace falta y tras verificar compatibilidad; sin imponer JUnit5 por cambiar imports.
- Fakes sirven para fallos y lógica aislada, no sustituyen archivos, DataStore real o pulso físico.
- Comandos con Wrapper: assembleDebug, testDebugUnitTest y lintDebug del módulo app; instrumentadas requieren entorno Android. Sólo se comunicó compilación debug (R08); pruebas unitarias, instrumentadas y lint no ejecutadas/registradas en esta sesión. Siguen únicamente los tests de plantilla, sin suite funcional nueva.
- Pruebas de proceso deben realizarse cuando las escrituras ya estén confirmadas para evaluar datos duraderos, y también con operaciones pendientes para documentar recuperación; no confundirlas con rotación.

## Registro a completar por ejecución

Copiar esta ficha dentro del documento por cada corrida significativa, sin marcar casos a partir de expectativas:

- Fecha, integrante y commit/rama probados:
- Dispositivo/emulador, modelo, Android/API y ajustes relevantes:
- Casos ejecutados y resultado observado:
- Capturas/logs depurados o test automatizado que sirve de evidencia:
- Fallos, enlace a tarea/PR y resultado de repetición tras corregir:
- Limitaciones (fake, sin dispositivo, caso no ejecutado):

No publicar rutas privadas, tokens o información personal en logs/capturas.

## Registro B — pruebas unitarias

**Fecha: 20/10/2026. Rama:** `feature/logica-datos`. **Ejecutado por:** asistente, a pedido de Claudio.

- **Entorno:** Windows, Gradle Wrapper, comando `:app:testDebugUnitTest` del módulo `app`. Sin dispositivo/emulador: son pruebas de JVM.
- **Resultado:** `BUILD SUCCESSFUL`. 57 pruebas, 0 fallos, 0 errores. Desglose: `PerfilValidatorTest` 23, `LocalAppRepositoryTest` 9, `SesionViewModelTest` 9, `MapeoPresentacionTest` 8, `PerfilViewModelTest` 7 y la prueba de plantilla 1.
- **Casos cubiertos por suite:** validación de nick/edad (P03–P05), errores tras Guardar y reactividad (P06), transiciones de sesión y admisión única (P10/P11), finalización tras guardado e idempotencia, reapertura/repetición sin duplicados y conservación de progreso al editar (P12–P14), marco sólo con el módulo completo, y mapeo de estados a presentación (orden/bloqueo de etapas, Descubrir sin opciones, Menos apoyo con turco oculto, logro sólo con el módulo completo).
- **Evidencia:** reporte HTML de Gradle en `app/build/reports/tests/testDebugUnitTest/`.
- **Limitaciones:** no acredita persistencia real en DataStore de dispositivo, ni recursos de cámara/háptica, ni el recorrido integrado en pantalla. La integración de navegación y pantallas está compilada, pero no se ejecutó en dispositivo: P01/P02 (recorrido en pantalla) y P07/P08 (Atrás y muerte de proceso) siguen pendientes de esa verificación; P15–P22 (cámara, háptica, rotación e integración física) permanecen pendientes de dispositivo.
- **Compilación:** `:app:compileDebugKotlin` y `:app:assembleDebug` ejecutadas con éxito antes de la suite.

La aceptación de háptica/cámara por el docente, según confirmación relatada por Felipe, sigue sin prueba física registrada.
