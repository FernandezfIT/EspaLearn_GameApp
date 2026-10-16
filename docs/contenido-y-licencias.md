# Contenido de demostración y licencias

[README](../README.md) · [Alcance](alcance-y-decisiones.md) · [Contratos](contratos.md)

**Actualización:16/10/2026.** Avatar y marco representados en UI; contenido bilingüe e ilustraciones del vocabulario aún pendientes.

## Catálogo mínimo acordado

Los IDs siguientes son la convención inicial propuesta para el catálogo técnico. No dependen del orden visual ni equivalen a recursos ya incorporados.

| ID | Temática | Español aprobado por Felipe | Turco | Ilustración |
|---|---|---|---|---|
| saludo_hola | Saludos | Hola | Pendiente de verificar | Escena de saludo; recurso pendiente. |
| saludo_adios | Saludos | Adiós | Pendiente de verificar | Escena de despedida; recurso pendiente. |
| alimento_pan | Alimentos | Pan | Pendiente de verificar | Ilustración de pan; recurso pendiente. |
| alimento_manzana | Alimentos | Manzana | Pendiente de verificar | Ilustración de manzana; recurso pendiente. |

No inventar traducciones ni atribuir revisión lingüística. Para pruebas de estado se permiten etiquetas sintéticas explícitas; no deben presentarse como contenido final verificado. B estructura el catálogo; ambos comprueban correspondencia texto/imagen/opciones.

## Configuraciones aprobadas

- Descubrir: ilustración + español + equivalente turco, Continuar sin pregunta.
- Con apoyo: ilustración + equivalente turco y dos opciones escritas en español.
- Menos apoyo: misma ilustración + dos opciones en español; turco oculto hasta Ver ayuda inline.
- Sin audio, timer, menú de temas ni nuevas pantallas. Cuatro unidades en tres etapas: 12 pasos/8 elecciones.
- Los saludos usan escenas: no reutilizar una imagen ambigua como si distinguiera automáticamente saludar/despedirse.

## Alternativas de práctica

Pendiente concretar las parejas de opciones y revisarlas para evitar ambigüedad. Propuesta técnica mínima: reutilizar Hola/Adiós como pareja y Pan/Manzana como pareja, cambiando el objetivo de cada unidad. No confundir propuesta de distractores con revisión pedagógica ni agregar vocabulario sólo para fabricar opciones.

Cada opción tendrá un ID y texto; la respuesta esperada se expresa mediante ID, no por posición izquierda/derecha. Mantener relación explícita entre gesto/botón y opción visible. No se exige aleatorización para EV2.

## Registro de recursos — completar antes de incorporar

| Recurso | Archivo/ID | Autor y origen | Licencia/permiso y atribución | Estado |
|---|---|---|---|---|
| Escena Hola | Pendiente | Pendiente | Pendiente | No incorporada. |
| Escena Adiós | Pendiente | Pendiente | Pendiente | No incorporada. |
| Pan | Pendiente | Pendiente | Pendiente | No incorporada. |
| Manzana | Pendiente | Pendiente | Pendiente | No incorporada. |
| Avatar predeterminado | ui/components/AvatarImagen.kt | Dibujo geométrico Canvas elegido por Felipe e incorporado con código guiado. | Creado en código para este caso; sin imagen externa descargada. | Representación con ojos/sonrisa incorporada en e2c8686; comprobada manualmente según Felipe. |
| Ilustración física para captura | Pendiente | Pendiente | Pendiente | Pendiente. |
| Marco opcional | AvatarImagen.kt; borde circular Compose | Implementación visual guiada, incorporada por Felipe. | Sin archivo de arte externo. | Con/sin marco representados y comprobados en fixtures; preferencia persistente pendiente B4. |

No basta encontrar una imagen en internet: registrar condiciones compatibles antes de añadirla a un repositorio público. Si se crea para el caso, indicar autoría y autorización. Recursos reutilizados no requieren duplicar archivos innecesariamente.

## Uso provisional en las pruebas A

`[Ilustración de prueba]`, `[Apoyo turco de prueba]` y Opción A/B son marcadores explícitos, no traducciones, escenas ni alternativas finales. `Hola` aparece en un escenario de Descubrir; eso no constituye el catálogo de cuatro unidades/12pasos.

No se capturó una ilustración ni se comprobó carga de un Painter externo. AvatarImagen sólo representa la geometría predeterminada o el Painter que reciba; la captura/archivo y su conservación pertenecen a B6. No confundir el avatar geométrico con las cuatro imágenes del vocabulario ni con una prueba de cámara.

## Revisión y privacidad

- Traducciones verificadas: ninguna registrada todavía.
- Revisión pedagógica: no realizada; consultar la nota de criterios pedagógicos.
- Usar nick inventado y edad simulada. No datos personales reales de menores, fotos/voz o identificadores reales.
- Fotografiar únicamente ilustraciones autorizadas; evitar documentos, personas u objetos identificadores en el encuadre de prueba.
- El contenido ampliado se plantea para EV3 por decisión del equipo; no se comprometió cantidad ni fecha.

## Checklist de incorporación

- [ ] Verificar equivalentes turcos con una fuente identificada y registrar fecha/origen.
- [x] Preparar avatar geométrico y marco visual en código, con origen documentado; sin archivos externos.
- [ ] Resolver las cuatro ilustraciones del vocabulario y la ilustración física de captura, con procedencia/autorización. Validar además el uso integrado de los siete elementos de la tabla.
- [ ] Concretar parejas de opciones y correspondencia de IDs.
- [ ] Verificar escenas de saludos y ayudas inline.
- [ ] Incorporar recursos autorizados y referencias en el catálogo.
- [ ] Registrar limitaciones lingüísticas/pedagógicas restantes sin afirmar validación inexistente.
