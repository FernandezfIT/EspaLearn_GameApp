# Contenido de demostración y licencias

[README](../README.md) · [Alcance](alcance-y-decisiones.md) · [Contratos](contratos.md)

**Cierre EV2 — rama `feature/cierre-ev2`:** cuatro ilustraciones geométricas incorporadas en Compose por elección explícita de Felipe, creadas con asistencia de IA para este proyecto, sin imágenes externas. Compilación y revisión visual del recorrido en Xiaomi confirmadas por Felipe; ver R10 en pruebas.md. Traducciones y adecuación educativa aún no verificadas; incorporar dibujos no acredita revisión lingüística ni pedagógica.

## Catálogo técnico implementado — 20/10

El catálogo vive en `app/src/main/java/com/example/espalearn/model/ContenidoUnidades.kt`. Los IDs son estables y se usan para la respuesta esperada; **no** por posición izquierda/derecha. Los textos y las parejas de opciones son la propuesta del equipo: no están validados lingüística ni pedagógicamente.

| ID | Temática | Español | Turco (propuesto) | Ilustración | Opciones (respuesta esperada en negrita) |
|---|---|---|---|---|---|
| hola | Saludos | Hola | Merhaba | Persona llegando a una casa; flecha hacia la entrada. | **Hola** / Buenas noches |
| adios | Saludos | Adiós | Hoşça kal | Persona alejándose de una casa; flecha hacia fuera. | **Adiós** / Gracias |
| pan | Alimentos | Pan | Ekmek | Pieza horneada dorada con cortes en la corteza. | **Pan** / Agua |
| manzana | Alimentos | Manzana | Elma | Fruta roja con tallo y hoja verde. | **Manzana** / Plátano |

Los equivalentes turcos son una propuesta técnica y **deben verificarse** con una fuente identificada antes de presentarlos como contenido revisado. Las parejas de opciones también requieren revisión para evitar ambigüedad.

### Conexión de las ilustraciones

- `Unidad.recursoIlustracion` contiene un ID lógico (por ejemplo `ilustracion_hola`), no un archivo drawable.
- `SesionViewModel` propaga esa referencia a `SesionUiState.recursoIlustracion` en cada paso.
- La Route usa `ui/components/IlustracionVocabulario.kt`; `ui/mapping/IlustracionUnidadUi.kt` relaciona ID, dibujo y descripción accesible de la escena.
- Los cuatro dibujos se escalan sin deformarse y no llevan rótulos con la respuesta española. El español se muestra en Descubrir y tras responder en práctica; pedir ayuda revela sólo el turco.
- `IlustracionPlaceholder` se conserva como respaldo para una referencia ausente o desconocida, no como contenido de las cuatro unidades actuales.
- Para sustituir un dibujo por un recurso externo, documentar primero autoría/licencia y cambiar el componente, manteniendo los IDs.
- **No** fotografiar menores ni usar imágenes personales reales como material de prueba.

## Convención inicial — histórico

Los IDs inicialmente propuestos (`saludo_hola`, `saludo_adios`, `alimento_pan`, `alimento_manzana`) no se usan en el código integrado. Los vigentes son `hola`, `adios`, `pan`, `manzana`, según el catálogo de arriba. Las ilustraciones son dibujos creados para EV2, no evidencia de que sus escenas sean inequívocas para el público objetivo.

No inventar traducciones ni atribuir revisión lingüística. Para pruebas de estado se permiten etiquetas sintéticas explícitas; no deben presentarse como contenido final verificado. B estructura el catálogo; ambos comprueban correspondencia texto/imagen/opciones.

## Configuraciones aprobadas

- Descubrir: ilustración + español + equivalente turco, Continuar sin pregunta.
- Con apoyo: ilustración + equivalente turco y dos opciones escritas en español.
- Menos apoyo: misma ilustración + dos opciones en español; turco oculto hasta Ver ayuda inline.
- Sin audio, timer, menú de temas ni nuevas pantallas. Cuatro unidades en tres etapas: 12 pasos/8 elecciones.
- Los saludos usan escenas: no reutilizar una imagen ambigua como si distinguiera automáticamente saludar/despedirse.

## Alternativas de práctica

Las parejas técnicas vigentes son las de la tabla del catálogo; la propuesta inicial de emparejar Hola/Adiós y Pan/Manzana no se materializó. Su revisión lingüística/pedagógica para evitar ambigüedad sigue pendiente. No confundir implementación de distractores con validación educativa.

Cada opción tendrá un ID y texto; la respuesta esperada se expresa mediante ID, no por posición izquierda/derecha. Mantener relación explícita entre gesto/botón y opción visible. No se exige aleatorización para EV2.

## Registro de recursos — completar antes de incorporar

| Recurso | Archivo/ID | Autor y origen | Licencia/permiso y atribución | Estado |
|---|---|---|---|---|
| Escena Hola | IlustracionVocabulario.kt / ilustracion_hola | Dibujo Canvas creado para EspaLearn con asistencia de IA, por elección de Felipe. | Sin arte externo; incorporación solicitada por Felipe. No se atribuye una licencia externa. | Incorporada; revisión visual comunicada por Felipe (R10). |
| Escena Adiós | IlustracionVocabulario.kt / ilustracion_adios | Dibujo Canvas creado para EspaLearn con asistencia de IA, por elección de Felipe. | Sin arte externo; incorporación solicitada por Felipe. No se atribuye una licencia externa. | Incorporada; revisión visual comunicada por Felipe (R10). |
| Pan | IlustracionVocabulario.kt / ilustracion_pan | Dibujo Canvas creado para EspaLearn con asistencia de IA, por elección de Felipe. | Sin arte externo; incorporación solicitada por Felipe. No se atribuye una licencia externa. | Incorporada; revisión visual comunicada por Felipe (R10). |
| Manzana | IlustracionVocabulario.kt / ilustracion_manzana | Dibujo Canvas creado para EspaLearn con asistencia de IA, por elección de Felipe. | Sin arte externo; incorporación solicitada por Felipe. No se atribuye una licencia externa. | Incorporada; revisión visual comunicada por Felipe (R10). |
| Avatar predeterminado | ui/components/AvatarImagen.kt | Dibujo geométrico Canvas elegido por Felipe e incorporado con código guiado. | Creado en código para este caso; sin imagen externa descargada. | Representación con ojos/sonrisa incorporada en e2c8686; comprobada manualmente según Felipe. |
| Ilustración física para captura | Pendiente | Pendiente | Pendiente | Pendiente. |
| Marco opcional | AvatarImagen.kt; borde circular Compose | Implementación visual guiada, incorporada por Felipe. | Sin archivo de arte externo. | Cambio de marco y conservación al reabrir comunicados por Felipe, R14/R15; sin nueva prueba de muerte de proceso verificada. |

No basta encontrar una imagen en internet: registrar condiciones compatibles antes de añadirla a un repositorio público. Si se crea para el caso, indicar autoría y autorización. Recursos reutilizados no requieren duplicar archivos innecesariamente.

## Uso provisional en las pruebas A — histórico del 16/10

`[Ilustración de prueba]`, `[Apoyo turco de prueba]` y Opción A/B son marcadores explícitos, no traducciones, escenas ni alternativas finales. `Hola` aparece en un escenario de Descubrir; eso no constituye el catálogo de cuatro unidades/12pasos.

En esas pruebas iniciales no se capturó una ilustración ni se comprobó carga de un Painter externo. En el cierre actual, Felipe sí confirmó captura, vista previa, cancelación y reapertura del avatar en su Xiaomi (R09 en pruebas.md). La revisión visual de los dibujos de vocabulario se confirmó después (R10). Ninguna de estas comprobaciones documenta el origen de la ilustración física usada.

## Revisión y privacidad

- Traducciones verificadas: ninguna registrada todavía.
- Revisión pedagógica: no realizada; consultar la nota de criterios pedagógicos.
- Usar nick inventado y edad simulada. No datos personales reales de menores, fotos/voz o identificadores reales.
- Fotografiar únicamente ilustraciones autorizadas; evitar documentos, personas u objetos identificadores en el encuadre de prueba.
- El contenido ampliado se plantea para EV3 por decisión del equipo; no se comprometió cantidad ni fecha.

## Checklist de incorporación

- [ ] Verificar equivalentes turcos con una fuente identificada y registrar fecha/origen.
- [x] Preparar avatar geométrico y marco visual en código, con origen documentado; sin archivos externos.
- [x] Incorporar cuatro dibujos de vocabulario en Compose con origen documentado y decisión explícita de Felipe; sin imágenes externas.
- [x] Comprobar visualmente los dibujos y su correspondencia en las tres etapas; confirmación de Felipe en Xiaomi, R10.
- [ ] Documentar procedencia/autorización de la ilustración física usada para captura.
- [ ] Concretar parejas de opciones y correspondencia de IDs.
- [x] Comprobar funcionamiento de escenas y ayuda inline en el recorrido, según R10; sin validación educativa.
- [x] Conectar las referencias del catálogo a los cuatro dibujos propios en la Route de Sesión.
- [ ] Registrar limitaciones lingüísticas/pedagógicas restantes sin afirmar validación inexistente.
