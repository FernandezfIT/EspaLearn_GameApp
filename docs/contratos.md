# Contratos entre las rutas A y B

[Volver al README](../README.md) · [Checklist](tareas-y-dependencias.md)

**Actualización:** 15/10/2026.

## Estado y propósito

Este documento registra los comportamientos de **K0–K6 aprobados por Felipe** durante la planificación. No acredita revisión de Claudio ni implementación, compilación o pruebas. Los nombres de tipos, firmas, scopes de ViewModels y mecanismos concretos siguen pendientes de coordinación técnica.

- **A — Felipe Fernández:** representa el estado y comunica acciones desde la interfaz.
- **B — Claudio Novoa:** produce estado, aplica validaciones y coordina acceso a datos.
- Compartir estos contratos permite preparar UI y lógica sin decidir firmas por ensayo y error.
- No incorporar reglas pendientes como si ya estuvieran aprobadas.

## K0 — Arranque

### Estados y respuesta de interfaz

| Estado entregado por B | Respuesta de A |
|---|---|
| Cargando | Mostrar indicador de carga; no decidir todavía si existe perfil. |
| Listo sin perfil | Mostrar el formulario de creación del perfil ficticio. |
| Listo con perfil | Mostrar Inicio con perfil y progreso recuperados. |
| Error de lectura | Mostrar error y acción Reintentar; no borrar datos ni asumir ausencia de perfil. |

**Acción acordada:** Reintentar solicita de nuevo la lectura tras un error.

### Criterios de verificación futuros

- [ ] Durante la lectura no aparece el formulario por interpretar un valor provisional como ausencia de perfil.
- [ ] Una lectura completada sin perfil permite crearlo.
- [ ] Una lectura completada con perfil permite entrar a Inicio.
- [ ] Un fallo permite reintentar sin borrar ni sobrescribir datos existentes.

**Relación EA2:** guía 11, representación reactiva del estado; guía 12, recuperación de datos locales.

## K1 — Perfil ficticio

Un único perfil local. Crear y editar reutilizan el formulario. No se requieren datos personales reales ni una cuenta.

### Estado que necesita la interfaz

| Elemento | Contenido acordado |
|---|---|
| Borrador | Nick ficticio, entrada de edad simulada y avatar actual/candidato. |
| Errores de campo | Información para mostrar el error junto al campo correspondiente. |
| Guardado en curso | Permite mostrar “Guardando…” y evitar envíos repetidos. |
| Resultado | Guardado confirmado o fallo recuperable. |
| Modo del formulario | Crear o editar, para aplicar el comportamiento de Atrás. |

La representación Kotlin concreta todavía no está definida. La entrada de edad debe poder representar un borrador vacío o inválido sin darlo por guardado.

### Acciones

- Editar nick.
- Editar edad simulada.
- Solicitar captura para reemplazar el avatar, mediante la frontera K5 descrita en este documento.
- Solicitar guardado.
- Volver mediante Atrás.

No habrá botón Cancelar ni selector de múltiples avatares en EV2.

### Reglas acordadas del nick

- Normalizar con NFC y contar **3–20 puntos de código Unicode**, ambos límites incluidos; no unidades Char ni promesa de contar grafemas visibles.
- Letras Unicode, incluidas letras españolas y turcas, y dígitos **ASCII `0–9`**.
- Sin espacios, guiones, símbolos ni emojis.
- No aplicar silenciosamente otras restricciones, unicidad o reglas de mayúsculas/minúsculas que no se hayan acordado.

**Regla técnica aprobada por Felipe:** NFC antes de validar/persistir; cada punto de código debe ser letra Unicode según Character.isLetter(int) o dígito ASCII 0–9. No recortar espacios automáticamente. Marcas combinantes que permanezcan separadas y no sean letras se rechazan. Se permite nick sólo numérico. Verificar casos representativos en JVM/Android; no se promete aceptación de todas las escrituras.

### Regla acordada de la edad simulada

- Valor guardado entero entre **7 y 10**, ambos incluidos.
- No guardar entrada vacía, no numérica, fraccionaria o fuera del rango previsto.
- Mantener los límites centralizados en la validación, no duplicados en las pantallas.

**Discrepancia académica abierta:** el caso escrito define público menor de 10 años, por lo que incluir 10 amplía ese alcance. Felipe decidió avanzar con 7–10 como propuesta y confirmarlo con el docente; ajustar si es necesario. No existe todavía aprobación docente comunicada para esta ampliación.

### Avatar y recursos

- Perfil nuevo con **un avatar ilustrado predeterminado**; no requiere selección manual para poder guardar.
- Sin banco seleccionable de múltiples avatares en EV2. Esa selección queda como evolución futura documentada, no como funcionalidad de esta entrega.
- Implementar cámara para sustituir el avatar por una fotografía de una ilustración autorizada.
- Cancelar o fallar la captura conserva el avatar anterior; no confundir la cancelación de cámara con Atrás en el formulario.
- Nunca fotografiar menores ni utilizar fotografías personales reales como material de prueba.
- K5 define el flujo funcional de captura; recuperación, referencias de archivos, limpieza y captura pendiente requieren concreción técnica antes de implementar.

**Aceptación docente comunicada:** Felipe confirmó que el docente acepta feedback háptico como segundo recurso junto con cámara. La pareja queda aceptada según ese relato; su implementación segura y pruebas reales siguen pendientes. K6 describe la señal háptica acordada.

### Guardado y errores

1. La UI comunica Guardar; no asume éxito por el toque.
2. La lógica valida y expone errores por campo si corresponde.
3. Durante el guardado se informa el estado y se evitan envíos repetidos.
4. Un error conserva el borrador para corregir o reintentar.
5. Sólo se sale como guardado exitoso tras confirmar la escritura.
6. Editar el perfil no debe borrar el progreso previamente guardado.

### Feedback de validación y fallos — requisito explícito

- **Nick:** indicar vacío, longitud menor de 3 o mayor de 20, y caracteres no permitidos.
- **Edad simulada:** indicar vacío, entrada no entera o valor fuera del rango propuesto 7–10.
- **Campos inválidos:** mostrar mensaje e indicador visual asociados al campo; no depender sólo del color. La lógica entrega los errores y la UI los representa.
- **Captura fallida:** mostrar un mensaje comprensible y permitir reintentar, conservando el avatar anterior y el resto del borrador.
- **Captura cancelada:** no tratarla como error ni perder el borrador.
- **Guardado fallido:** mostrar el fallo y permitir reintentar sin descartar datos ni navegar como si hubiera éxito.

**Momento aprobado:** no mostrar errores al abrir; pulsar Guardar activa la validación y, después, actualizar errores al corregir campos. Los textos finales se concretarán al implementar, sin cambiar las causas de error acordadas.

### Atrás — comportamiento aprobado

| Situación | Resultado |
|---|---|
| Crear perfil | Descartar borrador y salir de la actividad, sin guardar un perfil incompleto. |
| Editar perfil | Descartar cambios y volver a Inicio, conservando perfil y progreso guardados. |

**Durante guardado:** bloquear temporalmente Atrás y nuevos envíos hasta recibir resultado. Fallo conserva borrador y vuelve a habilitar reintento/Atrás; éxito continúa al destino previsto. Bloquear salida no crea atomicidad conjunta entre archivo y preferencias ni evita muerte del proceso.

### Criterios de verificación futuros

- [ ] Nick válido en los límites 3 y 20; rechazo fuera de límites y de caracteres excluidos.
- [ ] Letras españolas/turcas y formas equivalentes comprobadas conforme a la regla NFC/conteo de puntos de código ya aprobada.
- [ ] Edad 7 y 10 aceptadas conforme a propuesta; 6, 11, vacío, texto y fracciones rechazados.
- [ ] Avatar predeterminado disponible sin selector múltiple.
- [ ] Errores por campo y borrador conservado ante fallo.
- [ ] Guardado confirmado antes de la salida exitosa.
- [ ] Atrás al crear no persiste borrador; Atrás al editar conserva datos previos.
- [ ] Editar perfil conserva progreso.
- [ ] Cancelación/fallo de captura conserva avatar anterior; pruebas completas mediante K5.

**Relación EA2:** guía 11, formulario, validación y estado; guía 12, persistencia; guía 13, integración de cámara. Los datos personales del formulario de ejemplo no son requisitos del perfil ficticio del caso.

## K2 — Contenido mínimo de EV2

### Volumen aprobado

- **Un módulo** que combina **saludos y alimentos**.
- **Cuatro unidades de contenido:** dos de saludos y dos de alimentos.
- Reutilizar las mismas cuatro unidades en **Descubrir → Con apoyo → Menos apoyo**.
- **12 pasos totales:** cuatro de presentación y ocho de práctica con elección.
- Sin selector de temática, cursos independientes, desbloqueos por temática ni pantallas adicionales.
- La ampliación del contenido se pospone a EV3 como intención del equipo, no como exigencia docente confirmada.

### Datos mínimos acordados por unidad

- Identificador estable.
- Temática: saludos o alimentos.
- Texto en español y apoyo en turco.
- Ilustración autorizada.
- Dos opciones identificadas y respuesta esperada para los pasos de práctica.

**Unidades aprobadas:** Hola, Adiós, Pan y Manzana.

**Ayudas aprobadas:** Descubrir muestra ilustración, español y equivalente turco. Con apoyo muestra ilustración y equivalente turco con dos opciones escritas en español. Menos apoyo mantiene ilustración y opciones, oculta turco y permite revelarlo mediante Ver ayuda inline. Saludos utiliza escenas ilustradas. Sin audio ni pantallas extra.

Traducciones, imágenes y parejas de alternativas concretas siguen pendientes de verificar; propuesta mínima de parejas en contenido-y-licencias.md. No se ha realizado consulta pedagógica. Felipe decide priorizar demostración técnica para EV2; ver criterios-pedagogicos.md. Aprobar mecánica y volumen no acredita contenido validado.

### Criterios de verificación futuros

- [ ] Dos unidades por temática, sin selector ni flujo separado por tema.
- [ ] Cuatro unidades reutilizadas en las tres etapas: 12 pasos y 8 elecciones.
- [ ] Identificadores coherentes entre contenido, opciones y respuestas.
- [ ] Procedencia/autorización y revisión de los recursos documentadas.

**Relación EA2 y tareas:** modelos de guía 7 y componentes de guía 8; A5 y B1/B2.

## K3 — Sesión e interacciones

### Flujo aprobado

1. **Descubrir:** presentar contenido y botón Continuar, sin pregunta.
2. **Práctica:** elegir entre dos opciones mediante botón o swipe.
3. **Respuesta admitida:** bloquear otra elección y mostrar feedback.
4. **Continuar:** avanzar únicamente por acción manual después del feedback, sin avance automático.
5. **Último paso:** confirmar el guardado de la etapa antes de mostrarla como completada.

Botón y swipe representan la misma acción de elección. Deben controlarse eventos duplicados o de otra tarjeta/sesión. Los identificadores y mecanismos concretos se definirán con B.

Sin temporizador, vidas, puntuación mínima ni pantallas extra.

### Criterios de verificación futuros

- [ ] Descubrir no exige responder.
- [ ] Botón y swipe admiten una sola elección por paso.
- [ ] Un evento duplicado/tardío no registra otra elección ni avanza.
- [ ] El feedback permanece hasta pulsar Continuar.
- [ ] No se anuncia finalización confirmada antes de guardarla.

**Relación EA2 y tareas:** estado, interacción y animaciones de las guías 11/12; A5/A6 y B2.

## K4 — Progreso y logro

### Reglas aprobadas

- Tres hitos con acceso secuencial: **Descubrir → Con apoyo → Menos apoyo**.
- Completar requiere recorrer todos los pasos de la etapa, sin nota ni aciertos mínimos.
- Guardar etapas completadas, etapa activa y preferencia del marco; sin historial de respuestas ni estadísticas.
- Tras reabrir: conservar etapas completas y reiniciar desde el comienzo la etapa interrumpida.
- Permitir repetir etapas completadas sin borrar progreso ni duplicar etapas distintas completadas.
- Completar las tres etapas habilita un único logro final y un marco opcional para el avatar, sin pantalla adicional.
- Ante fallo de escritura: ofrecer reintento y no mostrar como confirmado un avance no guardado.

El logro se deriva de la finalización del módulo; no se añade un sistema de puntos o progresión independiente. El almacenamiento concreto y las transiciones compilables siguen pendientes.

### Criterios de verificación futuros

- [ ] Acceso secuencial y repetición coherentes.
- [ ] Errores de respuesta no impiden completar una etapa recorrida.
- [ ] Reapertura conserva etapas terminadas y reinicia la interrumpida.
- [ ] Repetir no duplica progreso ni logro.
- [ ] Marco opcional recuperable después de reabrir.
- [ ] Fallo de guardado permite reintentar sin éxito ficticio.

**Relación EA2 y tareas:** persistencia de guía 12; A7 y B4.

## K5 — Cámara y avatar

### Flujo aprobado

1. Mostrar el avatar predeterminado o el previamente guardado en el formulario.
2. **Tomar foto** abre la cámara para fotografiar una ilustración autorizada.
3. Una captura correcta se muestra como **avatar candidato del borrador**.
4. **Guardar perfil** confirma ese avatar junto con los demás campos válidos; tomar la foto no confirma por sí solo el cambio del perfil.
5. Cancelar o fallar la captura conserva el avatar anterior y permite intentarlo nuevamente. El fallo se comunica; cancelar no se presenta como error.
6. Atrás en el formulario descarta cambios, incluida la foto candidata, según K1.

Repetir la captura utiliza el mismo botón. Sin galería, recorte, filtros, selector múltiple ni pantalla adicional de confirmación.

### Responsabilidades y pendientes técnicos

- **B:** integración de captura y gestión del archivo.
- **A:** botón, representación de imagen, estado y mensajes.
- Concretar antes de implementar: referencia pendiente, conservación del archivo, recuperación tras recreación/proceso y limpieza de candidatos sin borrar imágenes vigentes.
- Mostrar una imagen no prueba que se haya conservado el archivo ni que sea recuperable al reabrir.
- Sólo ilustraciones autorizadas; nunca imágenes reales de menores.

### Criterios de verificación futuros

- [ ] Avatar inicial disponible sin selección.
- [ ] Captura correcta visible como candidata, no confirmada antes de Guardar.
- [ ] Validación de otros campos no pierde la candidata ni el borrador.
- [ ] Captura fallida muestra mensaje y reintento; cancelación no muestra error.
- [ ] Fallo/cancelación conservan el avatar anterior.
- [ ] Atrás descarta candidata sin reemplazar el perfil persistido.
- [ ] Guardado confirmado permite recuperar la nueva imagen al reabrir.
- [ ] Recuperación y limpieza probadas con archivos reales y dispositivo pertinente.

**Relación EA2 y tareas:** guía 13 y validaciones/feedback de guía 11; B6 y A8.

## K6 — Feedback háptico

### Reglas aprobadas

- Un pulso neutral cuando la lógica admite una elección, tanto por botón como por swipe.
- Mismo pulso para respuesta correcta o incorrecta.
- Sin pulso por Continuar, eventos duplicados o volver a mostrar la pantalla.
- Feedback visual independiente: el recorrido se entiende aunque no haya vibración disponible.
- Sin ajustes de intensidad ni pantalla adicional de configuración.

La implementación deberá respetar ajustes/capacidades del dispositivo y evitar repetir señales al recomponer o restaurar. Identidad y consumo de la señal se concretarán con B; no se han implementado ni probado.

### Criterios de verificación futuros

- [ ] Elección admitida produce una señal tanto con botón como con swipe.
- [ ] Acierto y error usan el mismo pulso neutral.
- [ ] Duplicados, Continuar y nueva presentación de pantalla no generan señales adicionales.
- [ ] El flujo sigue siendo comprensible y usable sin vibración.
- [ ] Funcionamiento comprobado físicamente, sin presentar simulación como prueba del recurso.

**Relación EA2 y tareas:** segundo recurso exigido por pauta EV2, aceptado por docente según confirmación de Felipe; B5 y A8, conectado a las acciones de K3.

## Pendientes antes de la implementación compartida

- Revisar los contratos con Claudio y fijar tipos, firmas y scopes de ViewModels.
- Implementar y probar las reglas ya aprobadas de NFC/conteo, errores tras Guardar y bloqueo de Atrás durante escritura.
- Completar contenido autorizado y verificación lingüística; ayudas y cuatro expresiones ya acordadas. Consulta pedagógica no realizada ni prevista por Felipe para EV2; discrepancia explícita con el caso, sin inventar dispensa.
- Confirmar con el docente la ampliación de edad propuesta 7–10.
- Definir detalles técnicos de persistencia, captura y consumo de señales, con pruebas de fallos.

**Cierre actual:** siete contratos de comportamiento aprobados por Felipe; no siete funcionalidades implementadas ni contratos técnicos compilables ya entregados. Base técnica, orden B1–B9 y convención inicial de tipos documentados en [ruta-b-claudio.md](ruta-b-claudio.md). PR pequeños desde ramas feature hacia develop con revisión del otro integrante; main sólo estable revisada.
