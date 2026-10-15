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

**Última actualización: 15/10/2026.** Esta sección se irá actualizando a medida que avance el desarrollo.

### Realizado

- Proyecto Android creado con Jetpack Compose y Material 3.
- Aplicación base ejecutada en el emulador, según la comprobación de Felipe.
- Organización inicial de paquetes: `ui`, `viewmodel`, `model` y `repository`.
- Subpaquetes `ui/screens` y `ui/components`, conservando el tema generado.
- Revisión inicial de exclusiones en `.gitignore`, incluida `.kotlin/`.
- Nombre acordado para el repositorio: `EspaLearn_GameApp`.
- README inicial.
- Primer commit publicado en [GitHub](https://github.com/FernandezfIT/EspaLearn_GameApp), según confirmación de Felipe.
- Ramas `main` y `develop` publicadas.
- Invitaciones enviadas a Claudio y al docente; aceptación pendiente de comprobar.
- Checklist y guía del entorno publicados en `develop`, según confirmación de Felipe.
- Comportamientos K0–K6 aprobados por Felipe y publicados en `develop` en el commit `1ce1867`; esta ampliación documental posterior aún no se publica.
- Guía de ruta B, alcance, catálogo pendiente, matriz y plan de pruebas redactados localmente y revisados/aprobados por Felipe; sin código de funcionalidades añadido. Revisión de Claudio y publicación de esta ampliación pendientes.
- Alcance mínimo EV2 acordado: un módulo de saludos y alimentos, cuatro unidades, 12 pasos y 8 elecciones.
- Cámara + háptica aceptadas como pareja de recursos por el docente, según confirmación de Felipe.

### Pendiente

- Confirmar aceptación de invitaciones y ejecución por Claudio.
- Mantener el checklist Markdown que reemplaza el tablero externo.
- Publicar la actualización K0–K6 y del alcance mínimo; concretar tipos, firmas y detalles técnicos con Claudio.
- Confirmar con el docente la ampliación propuesta a 7–10 años.
- Verificar traducciones y permisos de recursos. Consulta pedagógica no realizada ni prevista para EV2 según Felipe; mantener visible el desajuste con el caso.
- Claudio revisa la guía de ruta B y entrega B1 para desbloquear los consumidores de A.
- Implementar las pantallas y funcionalidades del MVP.
- Integrar y probar persistencia y recursos nativos.
- Verificar ejecución reproducible en el entorno de ambos integrantes.

**La aplicación actual conserva la pantalla de ejemplo generada.** La estructura de paquetes no equivale a una implementación completa de MVVM.

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
| **E1 — Flujo en memoria** | UI base, navegación, formulario y transiciones conectados; datos de prueba identificados como tales. | Pendiente. |
| **E2 — MVP con persistencia** | Recorrido del módulo y recuperación de perfil/progreso mediante almacenamiento real. | Pendiente. |
| **E3 — Interacción y robustez** | Gestos, animaciones, recursos aceptados, adaptabilidad y recuperación ante fallos. | Pendiente. |
| **E4 — Verificación y entrega** | Pruebas, documentación, revisión de requisitos y ejecución desde una copia limpia. | Pendiente. |

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
| Compatibilidad de fuentes/bytecode Java declarada | Java 11 |
| Criterio de selección de JVM mostrado por Android Studio | Versión 25 |

Los valores anteriores documentan la configuración observada; no constituyen una validación independiente de compatibilidad del conjunto. El criterio de JVM no identifica por sí solo la ruta efectiva del JDK utilizado.

### Ejecución desde Android Studio

1. Obtener una copia de [EspaLearn_GameApp](https://github.com/FernandezfIT/EspaLearn_GameApp) y seleccionar `develop` para integración. Consultar la [guía del entorno](docs/entorno-desarrollo.md) para la primera ejecución.
2. Abrir la carpeta raíz que contiene `settings.gradle.kts`.
3. Revisar el SDK y la JVM requeridos por el proyecto y completar la sincronización de Gradle.
4. Seleccionar el módulo `app` y un emulador o dispositivo compatible.
5. Ejecutar la aplicación con **Run**.

**Entorno de ejecución comunicado por Felipe:** emulador Pixel 6a con Android 17 / API 37.1. La ejecución base en ese emulador no demuestra todavía compatibilidad probada en API 33 ni funcionamiento de recursos físicos.

No se deben versionar rutas locales, credenciales ni claves de firma. No se debe confundir `gradle/`, que contiene el Wrapper y configuración compartida, con `.gradle/`, que contiene datos generados.

## Organización del código

Ubicación: `app/src/main/java/com/example/espalearn/`.

```text
espalearn/
├── MainActivity.kt
├── ui/
│   ├── theme/
│   ├── screens/
│   └── components/
├── viewmodel/
├── model/
└── repository/
```

- **ui:** presentación visual y comunicación de acciones.
- **viewmodel:** estado y lógica de presentación.
- **model:** tipos de datos y estados acordados entre las rutas.
- **repository:** acceso a datos y persistencia.

Los paquetes iniciales aún sin implementación se completarán al abordar las tareas correspondientes; no se crean clases vacías para aparentar avance. La organización parte de la guía 7 de EA2 y se ampliará según las necesidades acordadas.

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

**Para Claudio:** empezar por la guía de ruta B y el entorno. **Próxima sesión de Felipe:** iniciar A1/A2 según el punto de reanudación de alcance-y-decisiones.md, sin volver a decidir K0–K6.

La guía 7 fundamenta la organización y colaboración; las guías 8–10 orientan componentes, adaptabilidad y navegación; la guía 11, formularios y estado; la guía 12, persistencia y animaciones; y la guía 13, integración de cámara. La pauta EV2 determina las exigencias de entrega: repositorio público, documentación, participación real y funcionalidades verificadas.

**Seguimiento:** [checklist Markdown](docs/tareas-y-dependencias.md). **Repositorio:** [EspaLearn_GameApp](https://github.com/FernandezfIT/EspaLearn_GameApp).

## Privacidad y contenido

- Utilizar únicamente perfiles y datos ficticios.
- No recopilar ni incluir datos personales, fotografías, voces u otros identificadores reales de menores.
- La cámara propuesta se limita a ilustraciones autorizadas para la demostración, nunca a selfies de menores.
- Utilizar contenidos públicos con condiciones de uso compatibles, autorizados o creados para el caso, registrando su procedencia.
- No presentar entrevistas, revisión lingüística, aprobación docente o resultados de pruebas como realizados hasta contar con evidencia real.
