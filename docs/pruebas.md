# Plan y registro de pruebas

[README](../README.md) · [Contratos](contratos.md) · [Ruta B](ruta-b-claudio.md)

## Estado vigente del cierre

Rama local `feature/cierre-ev2`, base `427f1ef`, todavía sin commit/publicación del cierre. Últimos reportes inspeccionados: **99 pruebas JVM y 6 instrumentadas sin fallos/errores; lint con 0 errores y 16 advertencias**. Felipe ejecutó los comandos y comunicó las pruebas manuales en Xiaomi `25069PTEBG - 16`.

El apartado de cierre al final contiene los registros vigentes R09–R15. Los cortes del 16/10 y B y la tabla P01–P22 se conservan como planificación/evidencia histórica, no como estado actual de toda la app. No se afirma prueba desde copia limpia ni muerte de proceso verificada mediante PID.

## Estado histórico — 16/10

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

## Casos mínimos — plan original

Estados de la planificación inicial. Consultar el cierre y sus límites para los resultados posteriores; P13/P20 se matizan con la política de sesión retenida frente a nueva instancia acordada después.

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

## Herramientas y límites — referencia original

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

## Cierre EV2 — rama `feature/cierre-ev2`

Base: `develop` en `427f1ef` (PR #2 integrado). Estos cambios siguen locales, sin commit ni publicación.

- **Base verificada:** Felipe ejecutó `:app:assembleDebug :app:testDebugUnitTest` y comunicó `BUILD SUCCESSFUL`; reportes XML locales inspeccionados: 57 pruebas, 0 fallos, 0 errores.
- **Primer bloque verificado:** corrección de Atrás durante guardado de perfil/finalización y cierre de Activity al descartar creación. La práctica oculta la respuesta española hasta el feedback; la ilustración provisional no revela el objetivo. Tres nuevas pruebas de mapeo. Felipe repitió los comandos con `BUILD SUCCESSFUL`; reportes XML inspeccionados: 60 pruebas, 0 fallos, 0 errores. El bloqueo de Atrás aún requiere prueba integrada, no está acreditado por los tests JVM.
- **Segundo bloque verificado en JVM:** `AnswerHaptics` conectado al callback compartido de botón/swipe, sólo cuando `elegir` incrementa el contador. Sin observar/reproducir elecciones previas al montar la pantalla. Cinco pruebas nuevas sobre señales lógicas y admisión del ViewModel; no son pruebas instrumentadas del callback Compose ni de vibración física. Felipe comunicó `BUILD SUCCESSFUL`; reportes XML inspeccionados: 65 pruebas, 0 fallos, 0 errores.
- **Tercer bloque compilado y verificado en JVM:** Felipe comunicó `BUILD SUCCESSFUL`; reportes XML locales inspeccionados: 74 pruebas, 0 fallos, 0 errores. Prueba manual del recorrido de perfil/avatar confirmada por Felipe (R09). Vista previa de avatar candidato y carga del persistido en Inicio/Resumen, con miniatura en hilo IO y respaldo geométrico ante archivo ausente/ilegible. Identidad estable del ViewModel del formulario al guardar. Un archivo nuevo por intento, validación de imagen, fallos recuperables al abrir cámara y cancelación sin perder candidata anterior. Limpieza al guardar/descartar que conserva la referencia vigente incluso si su nombre empieza por `candidato_`. La confirmación es lógica al persistir ese nombre privado, sin renombrarlo. Seis pruebas JVM de archivos y tres de candidata/guardado/reintento añadidas; no acreditan FileProvider ni decodificación Android.
- **R09 — comprobación manual comunicada por Felipe:** app ejecutada en su teléfono Xiaomi; confirma que funciona el recorrido solicitado: crear/guardar perfil y llegar a Inicio, capturar una ilustración y ver la candidata, cancelar otro intento conservándola, guardar y cerrar/reabrir manteniendo el avatar. Modelo exacto y versión Android no informados. Es confirmación del usuario, no observación directa del asistente ni prueba instrumentada. No acredita muerte de proceso, fallos de almacenamiento ni háptica.
- **Cuarto bloque compilado y verificado en JVM:** Felipe comunicó `BUILD SUCCESSFUL`; reportes XML inspeccionados: 79 pruebas, 0 fallos, 0 errores. Cuatro dibujos propios Canvas (llegada, despedida, pieza horneada y fruta roja), elegidos por Felipe sin imágenes externas. Catálogo → `SesionUiState.recursoIlustracion` → Route → dibujo; respaldo explícito sólo ante referencia desconocida. Descripciones de escena sin rótulo de respuesta. Tres pruebas de correspondencia/descripciones y dos de conservación del recurso durante los 12 pasos, ayuda y feedback. No acreditan renderizado ni comprensión educativa.
- **R10 — recorrido/dibujos/háptica comunicado por Felipe:** en su Xiaomi confirma toda la prueba solicitada de Descubrir → Con apoyo → Menos apoyo: dibujos correctos y conservados en práctica, español oculto hasta elegir y un pulso al elegir por botón/swipe, sin pulso al Continuar. Confirmación manual del usuario, no prueba instrumentada ni validación educativa. No se atribuye prueba de ajustes hápticos, rotación ni persistencia de progreso/marco.
- **Quinto bloque compilado y verificado en JVM:** reportes XML locales inspeccionados: 85 pruebas, 0 fallos, 0 errores. Borrador de perfil con `SavedStateHandle` (modo, nick, edad, avatar actual/candidato, validación y fallos). Factory conectada al estado guardado del destino; esperar lectura inicial antes de crear Perfil/Sesión restaurados. Escritura interrumpida restaura formulario desbloqueado con aviso, no éxito ficticio. Icono geométrico de error por campo sin dependencia nueva. Seis pruebas JVM de reconstrucción del borrador; no equivalen a matar/restaurar el proceso Android.
- **Incidencia de navegación reportada por Felipe:** Continuar y Volver a Inicio en Resumen llevaban al mismo mapa; confirmado en código, ambos callbacks hacían el mismo `popBackStack`.
- **Sexto bloque compilado y verificado en JVM:** Felipe comunicó `BUILD SUCCESSFUL`; reportes XML inspeccionados: 93 pruebas, 0 fallos, 0 errores. Por decisión explícita de Felipe, Continuar abre la primera etapa pendiente y Repasar abre Descubrir sin borrar etapas/marco; Volver a Inicio conserva la salida al mapa. Desde Resumen se espera registro confirmado antes de navegar, se bloquean acciones/Atrás mientras se escribe, se muestra fallo recuperable y se retira Resumen de la pila al abrir sesión. Ocho pruebas JVM nuevas de selección y resultado de registro; no prueban la pila Navigation en dispositivo.
- **R11 — corrección de navegación comunicada por Felipe:** responde «Todo OK» tras solicitar compilación/suite y prueba de los botones en Xiaomi. Se registra su confirmación manual de navegación; en ese momento los XML locales todavía sumaban 85 pruebas. En la ejecución posterior ya se inspeccionaron las 93 pruebas del sexto bloque, todas aprobadas.
- **Lint verificado:** tras ejecutar `:app:lintDebug`, Felipe confirma éxito. Informes XML/TXT inspeccionados en `app/build/reports/lint-results-debug.*`: 0 errores, 16 advertencias (5 de versiones disponibles, 2 `VisibleForTests` por el valor predeterminado de SavedStateHandle, 1 etiqueta redundante, 1 condición SDK obsoleta y 7 colores sin uso). No se interpreta como cero advertencias ni se actualizan dependencias por estos avisos en el cierre.
- **R12 — rotación/formulario comunicada por Felipe:** en Xiaomi confirma que, tras poner nick inválido `ab`, pulsar Guardar, tomar una foto y girar, se conservan el borrador, la candidata y el error; aparece el icono de error. No equivale a muerte de proceso ni a persistir el borrador como perfil válido.
- **R13 — recuperación de borrador comunicada por Felipe:** tras la secuencia indicada de dejar la app en segundo plano, ejecutar `adb -d shell am kill com.example.espalearn` y regresar por Recientes, confirma recuperación del formulario, error y candidata. No es fuerza de detención ni desinstalación; no hubo comprobación independiente de PID por el asistente.
- **Séptimo bloque compilado y verificado:** Felipe comunicó `BUILD SUCCESSFUL`; XML inspeccionados: 93 pruebas JVM y 2 instrumentadas, sin fallos/errores. La ejecución Android incluyó `AppContainerRecreacionTest` y la prueba de contexto de plantilla, ambas aprobadas. Equipo identificado por el runner como `25069PTEBG - 16` (Xiaomi según Felipe). Lint sigue con 0 errores y 16 advertencias. La revisión detectó que MainActivity creaba otro AppContainer al recrearse mientras AppViewModel conservaba el repositorio anterior. Se mueve el contenedor a `EspaLearnApplication`, registrada en Manifest, y MainActivity lo reutiliza. Regresión instrumentada nueva `AppContainerRecreacionTest`: tras recrear Activity comprueba identidad del contenedor/repositorio/ViewModel y del StateFlow global. Prueba ejecutada en dispositivo; no está incluida en las 93 pruebas JVM. Verifica recreación de Activity, no muerte real del proceso.
- **Incidencia/ajuste de Resumen:** durante la comprobación final, Felipe observa que repetir cualquier etapa con módulo completo abre otra vez Resumen con marco. No se considera terminada esa comprobación de persistencia. El código abría Resumen tras cualquier finalización y mostraba el logro desde el estado acumulado.
- **Octavo bloque compilado y verificado:** Felipe ejecutó `assembleDebug`, `testDebugUnitTest`, `lintDebug` y `connectedDebugAndroidTest` con `BUILD SUCCESSFUL`. XML inspeccionados: 99 pruebas JVM y 4 instrumentadas, 0 fallos/errores; lint con 0 errores y 16 advertencias. Felipe aprueba Resumen automático sólo al primer cierre confirmado de etapa 3; etapas 1/2 y todos los repasos vuelven a Inicio. `SesionViewModel` compara el progreso al iniciar con el resultado confirmado y expone `moduloCompletadoPorPrimeraVez`; un cierre ya consumido no vuelve a emitirse con Continuar. Inicio habilita **Ver resumen** bajo la etapa 3 cuando el módulo está completo, también en dos columnas. Seis pruebas JVM nuevas de primer cierre/repaso/fallo/espera/duplicado y dos pruebas Compose instrumentadas de disponibilidad, posición y callback del botón, todas aprobadas. Las otras dos instrumentadas son recreación del contenedor y contexto de plantilla.
- **R14 — flujo actualizado comunicado por Felipe:** en Xiaomi confirma que terminar un repaso vuelve a Inicio y que **Ver resumen** abre el logro y permite cambiar el marco. La primera apertura automática al completar el módulo queda cubierta por lógica JVM, sin prueba manual nueva con datos iniciales vacíos.
- **R15 — conservación y reanudación comunicadas por Felipe:** tras la secuencia de aplicar marco, girar, editar nick, abrir Con apoyo y avanzar a paso 2, dejar en segundo plano, ejecutar `am kill` y regresar, informa que nick/progreso/marco se conservan, pero la sesión continúa en paso 2. Acepta conservar ese paso y no lo considera bloqueante. No se midió el PID ni se acredita que el proceso haya terminado. No se cambió código para persistir el índice: una sesión retenida puede continuar y una nueva instancia empieza en paso 1.
- **Noveno bloque compilado y verificado:** Felipe ejecutó assembleDebug, testDebugUnitTest, lintDebug y connectedDebugAndroidTest con BUILD SUCCESSFUL. XML inspeccionados: 99 pruebas JVM y 6 instrumentadas, 0 fallos/errores/omisiones; lint con 0 errores y 16 advertencias. `InicioRoute` espera `AppViewModel.prepararEtapa`, bloquea las etapas/edición/Resumen/Atrás mientras escribe y sólo solicita navegación con Exito. Fallo mantiene Inicio con mensaje y reintento. Se elimina el método asíncrono inseguro `registrarEtapaActiva`. Dos instrumentadas nuevas de la Route real con operación de escritura sustituida: fallo→sin navegación→reintento exitoso; escritura pendiente→acciones bloqueadas→navegación sólo al confirmar. No modifican datos del teléfono ni simulan un fallo nativo real de DataStore.
- **Estado de cierre:** implementación funcional y regresiones anteriores verificadas en la rama local, sin commit/publicación. Después de la última ejecución sólo se actualizó documentación. Quedan revisión de Claudio, publicación/PR, copia limpia, límites académicos/lingüísticos y ampliación de pruebas: fallos nativos, ajustes hápticos, API 33, primera celebración con datos vacíos y muerte real de proceso con PID. No se declara entrega ni certificación de todos esos casos.
