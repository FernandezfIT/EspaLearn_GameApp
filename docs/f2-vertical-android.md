# F2 — vertical Android, Room y cierres remotos

Corte18/11/2026. Implementación de la vertical de **tres palabras**, manteniendo MVVM en Android y MVC REST en Spring. No es todavía el catálogo150 ni el CRUD completo, Groq o release de entrega.

## 1. Función dentro de la app

```text
Perfil local confirmado (DataStore y archivos privados)
    ↓
Inicio consulta identidad demo, activo y progreso del servidor
    ↓ Comenzar / Retomar
Retrofit → Spring/MySQL: recuperar o crear lote fijo de tres
    ↓
Contenido válido en Room / catálogo propio → validar → guardar Room
    ↓
Descubrir → Con apoyo → Menos apoyo
    ↓ cada cierre
PUT con resultados → validación/commit MySQL → navegación
    ↓ cierre final
Resumen y cobertura remota; marco local heredado o habilitado por primer recorrido
```

No se reutiliza el progreso global EV2 como avance EV3. No hay generación IA ni solicitudes externas por gesto, opción o recomposición.

## 2. Estructura y archivos relevantes

| Capa | Archivos / responsabilidad |
|---|---|
| `data/remote/RecorridosApi.kt` | Retrofit, DTO y validación explícita de JSON/etapas |
| `data/local/ContenidoEntity.java`, `ContenidoDao.java`, `ContenidoDatabase.java` | Room/SQLite: instantáneas de contenido por palabra+versión |
| `data/local/PreferenciasEv3.kt` | Identidad, solicitud de inicio incierta, último recorrido, migración legada y preferencia de marco |
| `repository/ContenidoRoom.kt`, `VerticalRepository.kt` | IO del DAO y coordinación remoto/contenido, sin autoridad local de progreso |
| `viewmodel/RecorridoViewModel.kt` | Carga/error/reintento, preparación y cierre remoto; no contiene Retrofit/DAO |
| `navigation/AppNavHost.kt`, `SesionRecorridoRoute.kt` | Conexión del perfil existente, Inicio/Resumen nuevos y sesión reutilizada |
| `ui/screens/recorrido/RecorridoScreen.kt` | Representación de carga, permisos, etapas, cobertura y acciones |
| `SesionViewModel`, `SesionUiState`, `MapeoPresentacion`, `SesionScreen`, `Unidad` | Reutilizados y ampliados con resultados, cierre inyectado y explicación editorial |
| `app/AppContainer.kt`, `ViewModelFactories.kt` | Un cliente/Room/repositorios por proceso; almacenes aislables para pruebas |
| `platform/PermisoRedLocal.kt`, Manifest y configuración XML de red | Permiso LAN según soporte real y HTTP acotado sólo en debug |
| Backend `VerticalController`, `ContenidoService`, `EtapaService`, DTO/model/repository de cierre | Catálogo, progreso y confirmación atómica/idempotente |

Los archivos propios nuevos y modificados incluyen descripción de función/secciones. Se exporta el esquema Room1 en `app/schemas/com.example.espalearn.data.local.ContenidoDatabase/1.json`; es un artefacto JSON generado, no debe editarse ni llevar comentarios incompatibles con JSON.

## 3. Dependencias verificadas

- Retrofit y converter-gson3.0.0.
- Room runtime/compiler2.8.4; **entidades y DAO Java con annotationProcessor**, sin añadir KSP/KAPT.
- Se conserva AGP9.3.3, Wrapper9.5.0, Kotlin Compose2.2.10 y JUnit4 de Android.
- SpringBoot4.1.2/Java21/Maven y MySQL8.4.13 de F1.
- Room se ejecuta en Dispatchers.IO; no `allowMainThreadQueries`, no migración destructiva automática.

## 4. Endpoints mínimos añadidos

| Método/ruta | Contrato |
|---|---|
| GET `/api/v1/catalogo?version=prueba-3-v1` | Contenido editorial estructurado; bloquea catálogo incompleto |
| GET `/api/v1/aprendices/{id}/progreso?version=prueba-3-v1` | `completadasUnicas`, `totalPalabras`, `primerRecorridoCompletado` |
| PUT `/api/v1/aprendices/{id}/recorridos/{recorridoId}/etapas/{etapaId}` | CierreF0 con solicitudId, versión, etapa wire y tres resultados ordenados; devuelve recorrido confirmado |

Etapas wire: `descubrir`, `con_apoyo`, `menos_apoyo`, no nombres mayúsculos del enum Kotlin. Se conservan los endpoints crear/consultarF1.

El servidor valida dueño demo, etapa pendiente, versión, orden del lote y opciones por palabra. Descubrir exige opciónnull y ayudafalse; Con apoyo no registra el apoyo visible como pulsación de ayuda; Menos apoyo registra Ver ayuda. **No se envía ni se acepta un booleano de acierto que determine progreso; no hay nota mínima.**

El cierre tiene solicitud estable, huella de contenido y registro por etapa. Reintento conserva exactamente solicitud y resultados; cambiar UUID o contenido de un cierre ya aceptado produce409. No se permite reutilizar el UUID en otra etapa. Los cierres de un mismo aprendiz se coordinan mediante bloqueo SQL y transacción, igual que los inicios. Estado y resultados se confirman juntos; no se mantiene una transacción mientras se juega.

La cobertura se deriva de palabras únicas de recorridos NORMAL completos, no de las etapas ni de un booleano local. Datos demo: total3, no fingir150. En el código F2 sólo se habilita iniciar/retomar y cerrar; no DELETE ni repaso completo.

## 5. Contenido y caché

- V4 añade columnas editoriales y tabla de cierres sin borrar datosF1; V5, sólo perfil `demo`, completa las tres cartas. Las migracionesF1 no fueron alteradas.
- Pan/Ekmek, Manzana/Elma y Pera/Armut; explicaciones y opciones editoriales de demostración, no catálogo150 ni validación pedagógica acreditada.
- Se conservan dibujos Canvas propios de Pan/Manzana y se añade el de Pera con el mismo mecanismo. **No se descargó ni sustituyó todo el arte por OpenMoji**; el arte final/curación siguen pendientes. No es el rediseño de Claudio.
- Room conserva JSON editorial validado y referencia estable de imagen, no BLOB ni progreso. La lectura revalida IDs, versión, opciones y recurso local conocido.
- Si las tres cartas válidas están en Room, no se consulta de nuevo catálogo. Si la caché es parcial/inválida, se reconstruye sólo lo necesario y se preservan sus instantáneas válidas.
- Un recurso gráfico desconocido o respuesta incompleta bloquea la preparación; no presenta otra imagen como solución correcta.
- La lista preparada queda fija durante la etapa. La explicación sólo aparece en Descubrir o después de elegir; no revela la solución antes de responder en práctica.

## 6. Identidad, legado y reanudación

La misma instancia DataStore conserva perfil, foto/archivo privado y clavesEV2, añadiendo clavesEV3 sin sobrescribirlas. La migración nueva/versionada es atómica: conserva marco desbloqueado y su preferencia incluso si estaba desactivado. No convierte etapas antiguas en palabras completadas ni en primer recorridoEV3.

La identidad UUID demo se crea una sola vez, independiente de nick/edad. Editar perfil no cambia esa identidad ni borra Room o avance remoto. UUID no es autenticación; no publicar este backend sin un diseño de acceso adicional.

La solicitud de inicio se conserva hasta conocer su resultado. No hay cola offline ni sincronización de cierres en segundo plano. Al reabrir, se consulta el activo y su etapa pendiente; al abrir sesión nueva se empieza por la primera tarjeta de esa etapa, no se promete tarjeta exacta tras muerte de proceso.

La clave de ViewModel/eventos incluye recorrido+etapa. El cierre remoto se inyecta en la sesión existente y no llama al guardado de etapas local. Un fallo mantiene la sesión con error/reintento y el mismo cuerpo; navegar requiere confirmación. Atrás conserva el activo. Si Android restaura una ruta sin cartas preparadas, vuelve a Inicio en lugar de crear una sesión ficticia.

El marco heredado permanece visible a partir de sus preferencias aun si falla la lectura remota. Un logroEV3 nuevo sólo se habilita con progreso confirmado; aplicar/quitar es preferencia local. Los contadores de cobertura se actualizan consultando el servidor, no sumando localmente al pulsar.

## 7. Red y límite del emulador

Debug apunta a `http://10.0.2.2:8080/`. HTTP se permite **sólo para ese host en recursosdebug**; la configuración principal/release rechaza HTTP global y mantiene TLS. Release usa `https://example.invalid/` intencionalmente hasta configurar/probar su servidor enF6; no presentarla como release funcional.

Se declara INTERNET y ACCESS_LOCAL_NETWORK. Antes de acceder, se comprueba si el sistema realmente publica el permisoLAN y, cuando existe enAPI37+, se exige concesión; al volver de Ajustes se revalida, no se conserva un grant revocado en memoria.

**Entorno observado:** Pixel_6a/API37/Android17, fingerprint `google/sdk_gphone16k_x86_64/emu64xa16k:17/CP31.260623.012/16064790:user/dev-keys`, no publica ACCESS_LOCAL_NETWORK. La app no solicita un permiso inexistente y la conexión real al host funciona. Esto no acredita concesión/denegación/revocación en una imagen37 que sí implemente la protección. No se redujo el targetSDK ni se desactivó seguridad global para pasar las pruebas.

Referencia oficial contrastada: https://developer.android.com/privacy-and-security/local-network-permission . Verificar en imagen/dispositivo actualizado y con APKrelease antes de cerrar entrega.

## 8. Ejecutar y verificar

Usar instruccionesF1: `docker --context default compose --env-file infra/.env -f infra/compose.yaml up -d --wait mysql`, cargar entorno sin propagar MYSQL_ROOT_PASSWORD a Java y arrancar backend con perfil `demo` (`./mvnw spring-boot:run -Dspring-boot.run.profiles=demo`). Después Run→app en el emulador. No usar `localhost` dentro de Android para llegar al PC.

Backend, con servicio mysql-test iniciado y entorno local cargado:

```bash
cd backend/recorridos
./mvnw verify -Pintegration
```

Android desde raíz:

```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
# Backenddemo real en8080 y emulador operativo:
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.runBackendTests=true
```

Las dos pruebas instrumentadas de servidor son opt-in; sin el argumento se omiten explícitamente, no cuentan como integración acreditada. Si se habilitan y no hay backend, fallan. Las pruebas crean almacenes/identidades ficticias propios y eliminan sólo sus archivos de prueba; no limpian el perfil del usuario ni el volumen principal. El backenddemo puede conservar esas identidades de ensayo, sin datos personales reales.

### Resultados finales

- Android: **129 unitarias**,0fallos/0errores/0omitidas.
- Android: **9 instrumentadas**,0fallos/0errores/0omitidas, con `runBackendTests=true` y el build final de Spring.
- Backend: **8 unitarias y21 integraciones**,0fallos/0errores/0omitidas.
- Builddebug correcto; lint **0errores/20advertencias**. Las dos advertencias adicionales frente aF1 informan de versionesRoom más nuevas; no se actualizó el toolchain sólo para silenciarlas.
- UIreal: Inicio→preparación→nuevepasos/seiselecciones→cierre de tresetapas→Resumen, usando Retrofit/Room/Spring/MySQL. Algunas opciones deliberadamente incorrectas no bloquean el avance.
- Reapertura real de archivosRoom/DataStore: misma identidad/lote/etapa pendiente y marcolegado; coberturaEV3cero tras sóloDescubrir. **No se afirma muerte real del procesoAndroid**, ni revisión de cámara física.
- Backend: orden, opciones, secuencia, conflicto de cierre, solicitud reutilizada, propiedad, simultaneidad, coberturaúnica y catálogo; no se usóH2.
- Room: lectura/escritura, reemplazo por clave, versiones independientes y reapertura del archivoSQLite.
- Regresión de migraciones: el recorrido guardado en F1 antes deV4/V5 conserva exactamente su JSON completo, ID, palabras y orden al consultarlo en el servidorF2.
- Unitarias: cachehit, reconstrucción/cachéparcial, red503noausencia, rechazoimagen, resultados/reintentos, migración y estadosMVVM; dobles identificados como tales.

Reportes en `app/build/test-results/`, `app/build/outputs/androidTest-results/connected/`, `app/build/reports/` y `backend/recorridos/target/{surefire,failsafe}-reports/`. EsquemaRoom exportado en `app/schemas/`. Avisos de Unsafe(DataStore/JDK25) y agenteMockito son advertencias de herramientas, no fallos de test ni cobertura acreditada.

## 9. Pendientes y frontera

- F3: Groq real, cuentaFree, presupuesto, mensaje/persistencia y alternativa; no hubo llamadasGroq enF2.
- F4: DELETE/confirmación de descarte, reconciliación completa de resultados inciertos, estados ausentes tras reinicio/reemplazo del servidor, concurrencia entre operaciones distintas y matriz de regresión ampliada. No borrar perfil/ID para ocultar un404 remoto.
- F5: inventario150 revisado, arte/opciones/créditos y repaso completo, cobertura80% medida. Estos recuentos de tests no prueban ese80%.
- F6: host/release/permisoLAN efectivos, firma e instalación de ESAAPK, canales de llave/AVA/freeze; colaboración académica sigue sin darse por cumplida.

Al finalizar se detienen los serviciosSpring/MySQL iniciados para comprobar y se conserva el volumen principal. El emulador ya estaba en ejecución: no se cierra por iniciativa del asistente. Sin commit/push ni avance automático aF3.
