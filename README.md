# EspaLearn_GameApp

Aplicación Android educativa para apoyar el aprendizaje inicial de español de niños y niñas turcohablantes, mediante una experiencia breve de tarjetas e interacciones sencillas.

Proyecto académico de **Desarrollo de Aplicaciones Móviles (DSY1105)**. Primera entrega funcional orientada a la **Evaluación Parcial 2 (EV2)**.

> **Estado de la documentación:** este README se actualizará durante el desarrollo. Las funciones planificadas no deben interpretarse como implementadas. La consulta educativa que exige el caso no se realizó y Felipe no prevé realizarla para EV2 por tiempo: se prioriza la demostración técnica. No se afirma validación pedagógica ni dispensa documentada de ese requisito. Ver [alcance y discrepancias](docs/alcance-y-decisiones.md).

## Índice

- [Objetivo del proyecto](#objetivo-del-proyecto)
- [Integrantes y rutas de desarrollo](#integrantes-y-rutas-de-desarrollo)
- [Estado actual](#estado-actual)
- [Descripción del MVP — EV2](#descripción-del-mvp--ev2)
- [Objetivo técnico final y roadmap](#objetivo-técnico-final-y-roadmap)
- [Entorno y ejecución](#entorno-y-ejecución)
- [Organización del código](#organización-del-código)
- [Documentación del desarrollo](#documentación-del-desarrollo)
  - [Checklist de tareas y dependencias](docs/tareas-y-dependencias.md)
  - [Entorno de desarrollo](docs/entorno-desarrollo.md)
  - [Contratos A/B](docs/contratos.md)
  - [Guía de trabajo de Claudio — ruta B](docs/ruta-b-claudio.md)
  - [Alcance y decisiones](docs/alcance-y-decisiones.md)
  - [Contenido y licencias](docs/contenido-y-licencias.md)
  - [Situación pedagógica](docs/criterios-pedagogicos.md)
  - [Matriz EV2](docs/matriz-ev2.md)
  - [Plan y registro de pruebas](docs/pruebas.md)
- [Privacidad y contenido](#privacidad-y-contenido)

## Objetivo del proyecto

Apoyar el acercamiento inicial al español de niños y niñas turcohablantes mediante actividades breves, visuales y comprensibles, conectadas con situaciones cotidianas.

La aplicación busca ofrecer oportunidades de descubrir vocabulario, practicar con apoyo y volver a intentarlo con menos ayuda. No pretende ser un traductor universal ni reemplazar el acompañamiento educativo. La efectividad pedagógica no se considera demostrada por completar las actividades.

## Integrantes y rutas de desarrollo

| Integrante | Ruta | Responsabilidad principal prevista |
|---|---|---|
| **Felipe Fernández** | **A — Interfaz y experiencia** | Pantallas Compose, componentes visuales, navegación, formulario visual, tarjetas, gestos, animaciones, adaptabilidad y pruebas de interfaz. Preparación inicial del entorno y repositorio. |
| **Claudio Novoa** | **B — Lógica y datos** | Modelos, estados, ViewModels, validación, reglas del recorrido, persistencia local, integración técnica de recursos nativos y pruebas de lógica/datos. |

Ambos participan en decisiones de alcance, contratos entre componentes, contenido, revisión de cambios, integración, pruebas y documentación. El reparto no impide colaborar o reasignar tareas por acuerdo.

## Estado actual

**Última actualización: 16/10/2026.** Ruta A preparada con datos de prueba; integración con B pendiente.

### Preparado en la ruta A

- Perfil reutilizable para crear/editar, campos, errores recibidos, fallos de operación y controles deshabilitados durante guardado representado.
- Inicio y mapa de tres etapas; Sesión con Descubrir/Con apoyo/Menos apoyo, ayuda inline, opciones, feedback y Continuar; Resumen con logro y control del marco.
- Vistas de carga y error de arranque. Todavía no consumen una lectura real de perfil.
- Componentes reutilizables `CampoPerfil`, `MapaEtapas`, `TarjetaVocabulario`, `PanelFeedback`, `TarjetaDeslizable` y `AvatarImagen`.
- Swipe horizontal con umbral del 25 % del ancho al soltar y retorno animado; conectado a los mismos IDs de opción que los botones. No supone admisión única de respuesta por paso.
- Avatar geométrico predeterminado dibujado en código, marco opcional y entrada `Painter` para presentación futura de imágenes.
- Navigation Compose 2.9.7, cuatro destinos y contenedor `AppNavigation`; demo navegable en `src/debug` con datos prefijados y Atrás de prueba.
- Inicio con una o dos columnas según ancho disponible; escenarios compactos, amplios, horizontales y con fuente ampliada.
- Felipe incorporó el código y confirmó compilación debug y comprobaciones manuales. Resultados y límites en [pruebas](docs/pruebas.md).

**Base documental:** ampliación publicada en `develop` en `59046f9`, tras contratos `1ce1867`. **Base de código inspeccionada al cierre:** commit local `e2c8686` en `feature/base-ui`. No se afirma PR, revisión, merge ni publicación remota de esa rama.

### Pendiente de integración y verificación

- Entrega/revisión de B1, tipos compartidos, ViewModels, owners/factories y repositorio. Los tipos bajo `ui` son de presentación, no los modelos de B.
- Validación real, transiciones de sesión, control de duplicados/eventos tardíos y progreso confirmado.
- Perfil/progreso/marco duraderos, captura de ilustraciones y háptica reales.
- Conectar arranque y Routes a `MainActivity`, incluyendo lectura, errores, éxito de escritura y Atrás creando/guardando.
- Contenido bilingüe y recursos del vocabulario revisados; diferencia de edad 7–10 y demás pendientes académicos documentados.
- Confirmación de entorno/avances de Claudio, pruebas automatizadas y pruebas de integración, mínimo API 33 y recursos físicos.
- Revisar el indicador de error con icono por campo exigido en la pauta; actualmente hay apariencia y mensajes, sin un icono específico añadido.

**Run normal sigue mostrando la plantilla `Hello Android`.** Para inspeccionar la UI se usa **Run Preview → `NavegacionDemo`** en la variante debug. Guardar, Continuar, cámara y cambio de marco sólo comunican solicitudes en la demo; no simulan persistencia ni finalización exitosa.

**Cierre del 16/10:** se actualiza documentación y se espera el avance confirmado de B antes de continuar A. Por decisión de Felipe se prioriza funcionamiento y se aplaza TalkBack/accesibilidad avanzada salvo exigencia docente expresa; no se retiran los básicos ya implementados ni se registra TalkBack como probado.

## Descripción del MVP — EV2

El alcance aprobado por Felipe para esta primera versión es **un módulo educativo breve de saludos y alimentos**, no un juego completo ni una plataforma de cursos. Incluye **cuatro unidades de contenido, dos por temática**, reutilizadas en las tres etapas: **12 pasos totales, 8 con elección**. Sin selector de temática ni cursos separados. Los textos, traducciones, imágenes y ayudas concretos siguen pendientes de revisión pedagógica/lingüística.

La propuesta utiliza tarjetas con elecciones sencillas inspiradas en la interacción de Reigns, sin motores de videojuegos externos. El volumen se limita a demostrar la mecánica y más de una temática.

### Recorrido previsto

1. **Perfil ficticio:** crear o editar un único perfil local con nick inventado de 3–20 caracteres (letras Unicode y dígitos 0–9), edad simulada de 7–10 años como propuesta pendiente de confirmación docente y un avatar ilustrado predeterminado reemplazable mediante cámara. Sin selector múltiple en EV2; se reutiliza el mismo formulario y Atrás descarta el borrador. Ver [K1](docs/contratos.md#k1--perfil-ficticio) para el comportamiento de crear y editar.
2. **Inicio:** consultar un camino estático con tres etapas del módulo y el progreso registrado.
3. **Sesión de tarjetas:** recorrer las etapas Descubrir → Practicar con apoyo → Intentar con menos apoyo, reutilizando la pantalla de sesión. Las elecciones tendrán una alternativa mediante botones al gesto de deslizamiento.
4. **Resumen:** mostrar el recorrido completado y la siguiente acción disponible. Al finalizar el módulo, ofrecer un único logro y un marco opcional para el avatar.

### Comportamiento previsto

- Descubrir presenta contenido sin preguntas; las dos etapas de práctica ofrecen dos opciones por paso.
- Una elección admitida por botón o swipe bloquea nuevas respuestas y muestra feedback; Continuar avanza manualmente.
- Feedback háptico neutral e igual para acierto/error, sólo por elección admitida; sin pulso por Continuar, duplicados o volver a mostrar la pantalla.
- Retroalimentación visual comprensible aun sin vibración disponible.
- Completar una etapa significa recorrer sus tarjetas y recibir feedback, sin exigir una nota mínima.
- Acceso secuencial a las tres etapas; repetir una etapa completada no duplica ni borra progreso.
- Conservar las etapas completadas; al reabrir una etapa interrumpida, comenzar esa etapa desde el inicio.
- Persistir localmente perfil, progreso y preferencia del marco.
- Validaciones separadas de los componentes visuales: nick vacío/corto/largo/inválido y edad vacía/no entera/fuera de rango, con mensajes e indicadores por campo, no sólo color. Mostrar errores al pulsar Guardar y actualizarlos después al corregir.
- Nick normalizado NFC, 3–20 puntos de código, letras Unicode o dígitos ASCII; sin trim automático. Durante guardado bloquear Atrás y nuevos envíos hasta obtener resultado.
- Foto capturada como candidata del borrador, confirmada al guardar el perfil válido; sin galería, filtros, recorte ni confirmación adicional.
- Fallos de captura/guardado con mensajes y reintento, conservando datos anteriores y borrador según corresponda; cancelar cámara no se trata como error.
- Finalización de etapa y guardado de perfil sólo se muestran como exitosos tras confirmar la escritura.
- Animaciones vinculadas a la interacción y diseños adaptables a los tamaños de ventana que se prueben.

### Recursos nativos previstos

- **Cámara:** fotografiar una ilustración autorizada para reemplazar el avatar ficticio. El avatar predeterminado o la imagen anterior se conservan si se cancela o falla la captura.
- **Háptica:** confirmación neutral de una elección admitida, sin que la comprensión dependa de la vibración.

**Aceptación docente comunicada por Felipe:** cámara + feedback háptico sirven como pareja de recursos para EV2. Su implementación e integración segura y las pruebas, incluidas las físicas pertinentes, siguen pendientes; aceptación del alcance no equivale a funcionamiento verificado.

### Límites de esta versión

- Un perfil local, un módulo y cuatro unidades de saludos/alimentos; sin selector de temas.
- Sin historial de respuestas ni estadísticas; sólo progreso básico y preferencia del marco.
- Sin login, cuentas múltiples, backend ni servicios externos de traducción.
- Sin tienda, monedas, ranking, vidas ni adaptación automática de dificultad.
- Sin cursos adicionales ni pantallas de anuncios de funciones futuras.
- Unidades acordadas: Hola, Adiós, Pan y Manzana. Descubrir: imagen/español/turco; Con apoyo: imagen/turco y dos opciones en español; Menos apoyo: imagen/opciones y turco mediante Ver ayuda inline.
- Traducciones, ilustraciones autorizadas y pares concretos de opciones pendientes de verificar; sin atribuir validación pedagógica.

## Objetivo técnico final y roadmap

### Producto final previsto

Convertir el prototipo en una aplicación educativa Android coherente y mantenible: interfaz Compose, estado de presentación separado de la UI, acceso a datos mediante repositorios, almacenamiento local, navegación consistente y recursos nativos integrados de forma segura.

Este objetivo técnico describe **cómo se construirá y consolidará el producto**; es distinto del objetivo educativo de apoyar el aprendizaje inicial del español.

### Etapas de consolidación

| Etapa | Resultado previsto | Situación |
|---|---|---|
| **E0 — Base compartible** | Entorno reproducible, repositorio, seguimiento Markdown y contratos iniciales acordados. | Base publicada y K0–K6 de comportamiento aprobados por Felipe; ejecución de Claudio y contratos técnicos compilables pendientes. |
| **E1 — Flujo en memoria** | UI base, navegación, formulario y transiciones conectados; datos de prueba identificados como tales. | UI y navegación de demo preparadas; transiciones/validación de B y arranque real pendientes. |
| **E2 — MVP con persistencia** | Recorrido del módulo y recuperación de perfil/progreso mediante almacenamiento real. | Presentación preparada; contenido y persistencia reales pendientes. |
| **E3 — Interacción y robustez** | Gestos, animaciones, recursos aceptados, adaptabilidad y recuperación ante fallos. | Swipe/retorno, avatar y adaptabilidad probados manualmente con fixtures según Felipe; recursos y robustez integrados pendientes. |
| **E4 — Verificación y entrega** | Pruebas, documentación, revisión de requisitos y ejecución desde una copia limpia. | Registro manual y docs actualizados; suites, revisión conjunta, copia limpia y entrega pendientes. |

### Posibles evoluciones posteriores — fuera de EV2

- **Ampliación de contenido para EV3:** intención del equipo de incorporar más contenido tras demostrar la mecánica mínima en EV2; no se presenta como exigencia docente confirmada.
- **Selección de avatares:** ampliar el avatar predeterminado con un banco seleccionable de ilustraciones autorizadas; no se implementa en EV2.
- **Repaso mezclado:** explorar una modalidad de repaso del vocabulario trabajado.
- **Mini diálogos:** explorar actividades con intercambios cotidianos breves.
- Mejorar contenido, ayudas y experiencia según revisión pedagógica y retroalimentación.

Estas ideas son propuestas de evolución, **no funcionalidades disponibles ni compromisos con fecha**. Para EV2 se mantienen únicamente en la documentación/presentación, sin pantallas ni botones de “próximamente” dentro de la app. Cualquier ampliación requiere acordar alcance, criterios y esfuerzo.

## Entorno y ejecución

### Configuración declarada en el proyecto

| Elemento | Valor observado |
|---|---|
| Nombre local del proyecto | `EspaLearn_turco_espaniol` |
| Módulo | `app` |
| Namespace / application ID | `com.example.espalearn` |
| SDK mínimo | API 33 — Android 13 |
| SDK de compilación | API 37 |
| SDK objetivo | API 37 |
| Android Gradle Plugin | `9.3.3` |
| Gradle Wrapper | `9.5.0` |
| Plugin Kotlin Compose | `2.2.10` |
| Compose BOM | `2026.02.01` |
| Navigation Compose | `2.9.7` |
| Compatibilidad de fuentes/bytecode Java declarada | Java 11 |
| Criterio de selección de JVM mostrado por Android Studio | Versión 25 |

Los valores anteriores documentan la configuración observada; no constituyen una validación independiente de compatibilidad del conjunto. El criterio de JVM no identifica por sí solo la ruta efectiva del JDK utilizado.

### Ejecución desde Android Studio

1. Obtener una copia de [EspaLearn_GameApp](https://github.com/FernandezfIT/EspaLearn_GameApp) y seleccionar `develop` para integración. Consultar la [guía del entorno](docs/entorno-desarrollo.md) para la primera ejecución.
2. Abrir la carpeta raíz que contiene `settings.gradle.kts`.
3. Revisar el SDK y la JVM requeridos por el proyecto y completar la sincronización de Gradle.
4. Seleccionar el módulo `app` y un emulador o dispositivo compatible.
5. Ejecutar la aplicación con **Run**.

**Entorno actual comunicado por Felipe:** Windows y emulador Pixel 6a con Android 17 / API 37.1. También reportó funcionamiento del swipe en ejecución real; modelo/API de ese equipo sin registrar. No implica pruebas de cámara/háptica ni compatibilidad comprobada en API 33.

**Explorar el avance A:** abrir `app/src/debug/java/com/example/espalearn/preview/NavegacionDemo.kt` y ejecutar **Run Preview** en el emulador/dispositivo. Las demás previews aíslan errores, guardando, ayuda, marco y tamaños. No conectar la demo a la entrada release ni interpretar el botón «Abrir Resumen de prueba» como etapa completada. Véase [entorno](docs/entorno-desarrollo.md).

No se deben versionar rutas locales, credenciales ni claves de firma. No se debe confundir `gradle/`, que contiene el Wrapper y configuración compartida, con `.gradle/`, que contiene datos generados.

## Organización del código

Ubicación: `app/src/main/java/com/example/espalearn/`.

```text
espalearn/
├── MainActivity.kt                  # plantilla; conexión real pendiente
├── navigation/
│   ├── AppDestination.kt
│   └── AppNavigation.kt
└── ui/
    ├── theme/
    ├── screens/{arranque,perfil,inicio,sesion,resumen}/
    └── components/

app/src/debug/java/com/example/espalearn/preview/
└── previews, DatosNavegacionDemo.kt y NavegacionDemo.kt
```

- **ui:** presentación visual y comunicación de acciones.
- **navigation:** destinos y contenedor reutilizable; no realiza validaciones ni guarda datos.
- **viewmodel, model y repository:** responsabilidades previstas de B, aún sin implementación integrada en esta base.
- **debug/preview:** escenarios sintéticos y navegación demostrativa; no son la entrada final del producto.

`EtapaMapaUi`, `SesionPresentacion`, `OpcionRespuestaUi`, `FeedbackUi`, `ResumenPresentacion` y `LogroResumenUi` son proyecciones de UI. No sustituyen el modelo/estado compartido de B. No se crean clases vacías para aparentar avance. La organización parte de la guía 7 de EA2 y se ampliará según las necesidades acordadas.

## Documentación del desarrollo

La carpeta `docs/` concentra el seguimiento y las instrucciones del equipo; los documentos adicionales se crearán cuando se trabaje su contenido.

**Seguimiento acordado:** según la indicación docente comunicada por Felipe el 15/10/2026, no se requiere Trello ni otra plataforma. Se utilizará un checklist Markdown actualizado por ambos integrantes, manteniendo evidencia real de participación.

### Documentos disponibles

| Documento | Contenido |
|---|---|
| [Checklist de tareas y dependencias](docs/tareas-y-dependencias.md) | Etapas E0–E4, reparto A/B, dependencias, criterios de cierre y casillas de avance. |
| [Entorno de desarrollo](docs/entorno-desarrollo.md) | Configuración observada, primera ejecución, archivos compartidos y comprobaciones pendientes por integrante. |
| [Contratos A/B](docs/contratos.md) | Comportamientos K0–K6 aprobados por Felipe, criterios de prueba futuros, discrepancias y detalles técnicos pendientes. |
| [Guía de trabajo de Claudio — ruta B](docs/ruta-b-claudio.md) | Primeros pasos, B1–B9, convención de tipos, almacenamiento, recursos y entregas a Felipe. |
| [Alcance y decisiones](docs/alcance-y-decisiones.md) | MVP, acuerdos, discrepancias y punto de reanudación de la ruta A. |
| [Situación de criterios pedagógicos](docs/criterios-pedagogicos.md) | Declaración de consulta no realizada y límites; no contiene entrevista ni validación inventada. |
| [Contenido y licencias](docs/contenido-y-licencias.md) | Cuatro unidades acordadas y registro de traducciones, imágenes y permisos todavía pendientes. |
| [Matriz EV2](docs/matriz-ev2.md) | Indicadores, tareas, evidencia pendiente y diferencias académicas. |
| [Plan y registro de pruebas](docs/pruebas.md) | Casos futuros y ficha de resultados; no presenta pruebas no ejecutadas como superadas. |

Los documentos están redactados; su contenido distingue decisiones, propuestas técnicas y verificaciones pendientes. Documentación disponible no significa implementación o revisión externa completada.

**Para Claudio:** revisar la guía de ruta B, los contratos y la UI ya preparada; entregar B1 y los checks correspondientes sin duplicar pantallas. **Próxima sesión de Felipe:** esperar su aviso de avance B, revisar lo entregado y conectar por bloques según [reanudación](docs/alcance-y-decisiones.md#reanudación-de-felipe--próxima-sesión), sin repetir A1/A2 ni redecidir K0–K6.

La guía 7 fundamenta la organización y colaboración; las guías 8–10 orientan componentes, adaptabilidad y navegación; la guía 11, formularios y estado; la guía 12, persistencia y animaciones; y la guía 13, integración de cámara. La pauta EV2 determina las exigencias de entrega: repositorio público, documentación, participación real y funcionalidades verificadas.

**Seguimiento:** [checklist Markdown](docs/tareas-y-dependencias.md). **Repositorio:** [EspaLearn_GameApp](https://github.com/FernandezfIT/EspaLearn_GameApp).

## Privacidad y contenido

- Utilizar únicamente perfiles y datos ficticios.
- No recopilar ni incluir datos personales, fotografías, voces u otros identificadores reales de menores.
- La cámara propuesta se limita a ilustraciones autorizadas para la demostración, nunca a selfies de menores.
- Utilizar contenidos públicos con condiciones de uso compatibles, autorizados o creados para el caso, registrando su procedencia.
- No presentar entrevistas, revisión lingüística, aprobación docente o resultados de pruebas como realizados hasta contar con evidencia real.
