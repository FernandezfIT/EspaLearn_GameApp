# Plan y registro de pruebas

[README](../README.md) · [Contratos](contratos.md) · [Ruta B](ruta-b-claudio.md)

## Estado real

Sólo se ha comunicado ejecución de la pantalla base por Felipe en el emulador. **Los casos siguientes no han sido ejecutados.** Tener un test o una pantalla con datos de ejemplo no acredita persistencia, recursos físicos ni aprendizaje.

Ambos integrantes disponen de teléfono Android físico; registrar modelo/API antes de probar. No usar datos, fotos ni voces reales de menores.

## Casos mínimos

| ID | Caso y resultado esperado | Responsable principal | Tipo | Resultado |
|---|---|---|---|---|
| P01 | Cargando no muestra formulario prematuramente; lectura sin/con perfil entra al destino correcto. | B + A | Integración K0 | Pendiente |
| P02 | Error de lectura muestra Reintentar sin convertir datos existentes en perfil vacío. | B | Fake de fallo + integración | Pendiente |
| P03 | Nick NFC de 3/20 puntos de código aceptado; 2/21, vacío, espacios y símbolos rechazados. | B | Unitario | Pendiente |
| P04 | Letras españolas/turcas y formas equivalentes; sólo dígitos ASCII admitidos y nick numérico permitido; marcas separadas rechazadas. | B | Unitario + casos Android | Pendiente |
| P05 | Edad 7/10 aceptada como propuesta; 6/11, vacío, texto y fracción rechazados. | B | Unitario | Pendiente |
| P06 | Sin errores al abrir; Guardar inválido muestra mensajes/indicadores por campo; correcciones posteriores actualizan errores. | A + B | UI/integración | Pendiente |
| P07 | Guardando bloquea doble envío y Atrás; fallo conserva borrador y habilita reintento; éxito confirmado precede navegación. | B + A | Integración con fallo | Pendiente |
| P08 | Atrás creando descarta y sale; editando vuelve a Inicio conservando perfil/progreso previos. | A + B | UI/datos | Pendiente |
| P09 | Cuatro unidades, dos temas y 12 pasos: Descubrir sin elección, ocho elecciones en práctica y ayudas acordadas. | A + B | Recorrido | Pendiente |
| P10 | Botón/swipe tienen mismo efecto; duplicado o evento de otra sesión/paso no registra otra elección. | B + A | Unitario/UI | Pendiente |
| P11 | Feedback espera Continuar; no timer ni avance automático; error de respuesta no impide completar. | A + B | UI | Pendiente |
| P12 | Acceso secuencial; finalizar espera escritura; fallo permite reintentar sin éxito ficticio. | B + A | Datos/recorrido | Pendiente |
| P13 | Reapertura conserva etapas completas y reinicia interrumpida; repetición no duplica progreso/logro. | B | Integración persistencia | Pendiente |
| P14 | Logro sólo al completar módulo; marco opcional persiste al reabrir; editar perfil conserva avance. | B + A | Integración | Pendiente |
| P15 | Cámara produce candidata; perfil no cambia hasta Guardar válido; imagen guardada recuperable al reabrir. | B + A | Dispositivo/archivo | Pendiente |
| P16 | Cancelar cámara no es error; fallo muestra reintento; ambos conservan avatar/borrador. Atrás descarta candidata. | B + A | Dispositivo/archivo | Pendiente |
| P17 | Recreación durante captura, archivo ausente y huérfanos se recuperan/limpian sin borrar referencia vigente ni progreso. | B | Integración/robustez | Pendiente |
| P18 | Un pulso neutral por elección admitida, igual acierto/error y botón/swipe; ninguno por Continuar/duplicados/restauración. | B + A | Teléfono físico | Pendiente |
| P19 | Recorrido comprensible sin vibración disponible y respetando ajustes del dispositivo. | B + A | Teléfono físico | Pendiente |
| P20 | Rotación y muerte de proceso se prueban por separado; datos confirmados permanecen y se aplica política de reanudación. | B + A | Android | Pendiente |
| P21 | Texto ampliado, teclado, ventanas compacta/ampliada, foco/etiquetas y botones alternativos sin acciones ocultas. | A | UI/accesibilidad | Pendiente |
| P22 | Claudio sincroniza, compila y ejecuta desde su copia; probar mínimo API33 además del emulador de Felipe. | Ambos | Reproducibilidad | Pendiente |

## Herramientas y límites

- Base local existente: JUnit4 para unitarias y configuración de tests instrumentados/Compose generada. No afirmar que se ejecutó la suite.
- Añadir coroutines-test u otra dependencia sólo si hace falta y tras verificar compatibilidad; sin imponer JUnit5 por cambiar imports.
- Fakes sirven para fallos y lógica aislada, no sustituyen archivos, DataStore real o pulso físico.
- Propuestas de comandos con Wrapper: assembleDebug, testDebugUnitTest y lintDebug del módulo app; instrumentadas requieren entorno Android. No se ejecutaron en esta sesión documental.
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
