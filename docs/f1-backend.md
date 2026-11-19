# F1 — servidor mínimo de recorridos

> Corte histórico F1. Estado posterior: [F2, Android conectado y cierre de etapas](f2-vertical-android.md). Las referencias siguientes a Android aún no conectado corresponden al alcance de F1.

Implementado y comprobado en Linux Mint el 18/11/2026. Alcance: crear y consultar recorridos persistentes de tres palabras. **Todavía no conecta Android, Room ni Groq; tampoco implementa cierre de etapas, descarte, progreso o repaso completo.**

## 1. Función dentro de EspaLearn

En F2, Inicio solicitará un recorrido a este servidor mediante Retrofit. Spring recuperará el activo o seleccionará tres palabras, guardará selección y orden en MySQL y devolverá el resultado confirmado. Cambiar de pantalla o repetir una petición no autoriza otro sorteo. Android nunca accede directamente a MySQL.

F1 prueba esa parte mediante HTTP real. No acredita todavía el CRUD completo desde la interfaz exigido por EV3.

## 2. Estructura MVC del backend

```text
backend/recorridos/
├── pom.xml, mvnw, mvnw.cmd, .mvn/wrapper/
└── src/
    ├── main/java/com/espalearn/recorridos/
    │   ├── RecorridosApplication.java
    │   ├── controller/   # RecorridoController y traducción pública de errores
    │   ├── dto/          # Contratos HTTP de entrada, salida y error
    │   ├── service/      # Reglas, selección, transacción y deduplicación
    │   ├── model/        # Aprendiz, Palabra, Recorrido y SolicitudInicio
    │   └── repository/   # Consultas JPA/SQL y bloqueo por aprendiz
    ├── main/resources/
    │   ├── application.yaml, application-demo.yaml
    │   └── db/          # Migraciones del esquema y fixture demo
    └── test/java/       # Unitarias del servicio e integración HTTP + MySQL
infra/
├── compose.yaml         # MySQL principal y servicio de pruebas optativo
├── .env.example         # Plantilla sin credenciales reales
└── .env                 # Local, ignorado, permisos600; no compartir
```

En esta API REST la representación de salida es JSON mediante DTO, no una vista HTML. Controller adapta HTTP; Service aplica reglas; Repository accede a los modelos persistidos. No se incluyó Spring en `settings.gradle.kts` ni se alteró el MVVM Android.

Los archivos propios tienen comentarios de función y secciones. Los scripts oficiales Maven Wrapper conservan licencia/comentarios y añaden una descripción de su papel en EspaLearn.

## 3. Versiones y configuración

| Pieza | Valor verificado |
|---|---|
| Java backend |21.0.13 |
| Spring Boot |4.1.2 |
| Maven Wrapper |wrapper3.3.4, distribuciónMaven3.9.11 con SHA256 fijado |
| MySQL ejecutado |8.4.13 |
| Imagen |`mysql:8.4@sha256:a81d043930e51745522bd5c430961cf8286f0a23f0a9a2a6e0108f7ea71b0902` |
| Docker / Compose |29.10.0 / v5.1.3, contexto `default` |
| Backend PC |`http://127.0.0.1:8080` |
| Futuro acceso desde emulador |`http://10.0.2.2:8080`, a verificar en F2 |
| SQL principal / pruebas |loopbackPC3307 /3308 |

`SERVER_PORT`, `SERVER_ADDRESS`, `DB_URL`, `DB_USER` y `MYSQL_PASSWORD` configuran el proceso Spring. La demo escucha sólo en loopback por defecto. No exponerla a Internet: el UUID es identidad ficticia, **no autenticación**. No hay CORS global permisivo ni credenciales Groq en F1.

## 4. Arrancar desde la raíz del repositorio

Requisitos: Java21, Docker Engine operativo, Compose y conexión inicial para descargar Maven/dependencias/imagen. Los ejemplos Bash usan el contexto comprobado en este equipo sin cambiar el contexto global.

Si no existe `infra/.env`, copiar `.env.example`, poner contraseñas aleatorias diferentes y restringir permisos600. En esta ejecución el asistente ya creó ese archivo con valores aleatorios no mostrados. No reemplazarlo por los placeholders.

```bash
# MySQL principal; puerto accesible sólo desde el PC.
docker --context default compose --env-file infra/.env -f infra/compose.yaml up -d --wait mysql

# Cargar entorno local al proceso, sin propagar la contraseña root a Java.
set -a
. ./infra/.env
set +a
unset MYSQL_ROOT_PASSWORD

# Backend independiente. El perfil demo habilita explícitamente los tres IDs de prueba.
cd backend/recorridos
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

También puede ejecutarse desde el IDE con Java21, perfil `demo`, `MYSQL_PASSWORD` y el resto de variables pertinentes. El archivo `.env` de Compose **no se carga automáticamente en Spring**. Si se construye el JAR con `./mvnw package`, se puede ejecutar `java -jar target/recorridos-0.1.0-SNAPSHOT.jar --spring.profiles.active=demo`.

El perfil demo incluye `prueba-3-v1` con Pan, Manzana y Pera como identificadores/textos de prueba; no certifica traducciones, arte ni revisión editorial de 150 entradas. Sin ese perfil, sobre una base nueva, sólo se aplica el esquema y no se inventa catálogo. Mantener el perfil `demo` al reutilizar esta base: sus migraciones incluyen el fixture V2. Cambiar los perfiles de migración sobre el mismo esquema requiere una estrategia explícita; no borrar el historial ni activar reparación automática para saltarse una validación.

## 5. Peticiones implementadas

Base: `/api/v1/aprendices/{aprendizId}/recorridos`. Los identificadores HTTP deben ser UUID válidos. No usar literalmente los IDs descriptivos de los fixtures JSON F0.

| Método | Ruta | Resultado |
|---|---|---|
| POST |base |201 nuevo + Location;200 recuperación compatible |
| GET |`/activo` |200 activo;204 si no existe |
| GET |`/{recorridoId}` |200 propio;404 ausente o perteneciente a otra identidad |

Ejemplo, tras arrancar el servidor:

```bash
curl -i http://127.0.0.1:8080/api/v1/aprendices/11111111-1111-4111-8111-111111111111/recorridos \
  -H 'Content-Type: application/json' \
  -d '{"solicitudId":"22222222-2222-4222-8222-222222222222","tipo":"NORMAL","versionCatalogo":"prueba-3-v1"}'
```

Respuesta: `recorridoId`, `versionCatalogo`, `tipo`, `estado`, tres `palabraIds` ordenados y `etapasConfirmadas` vacías en un inicio. No enviar `palabraIds` en el POST: se rechaza como campo desconocido. No se serializan entidades ni datos privados.

Repetir el mismo UUID de solicitud y contenido devuelve el resultado existente. Solicitud nueva compatible también recupera el activo y queda asociada a él. Reutilizar solicitud con otro contenido o pedir activo de otro tipo/versión devuelve409; no sustituye el lote.

Errores públicos: `{codigo,mensaje,campos}`. JSON/UUID/campos inválidos400; recurso ausente404; conflicto409; persistencia no disponible503. La ausencia204 no oculta fallos de BD. `REPASO` se rechaza en F1: su elegibilidad real y ejecución pertenecen a etapas posteriores.

## 6. Persistencia y concurrencia

- MySQL InnoDB, volumen `espalearn_mysql-data` y migraciones Flyway versionadas.
- Hibernate valida el esquema; no se usa `create`, `create-drop` ni regeneración destructiva.
- V1 crea tablas/índices/FKs; V2, sólo demo, inserta tres identificadores; V3 establece comparación exacta de versiones/IDs editoriales. No editar migraciones ya aplicadas: añadir una nueva.
- Un upsert y bloqueo de fila por aprendiz serializan creación/consulta de activos dentro de la transacción.
- Una columna generada con índiceUNIQUE impide dos activos por aprendiz también a nivelSQL.
- Solicitudes únicas por aprendiz+solicitud preservan la asociación con el resultado. No basta con ponerUUID a la petición.
- La tabla de selección guarda posición y evita duplicar palabras dentro del recorrido. El servicio exige exactamente tres.
- NORMAL excluye palabras de recorridos NORMAL completados. En las pruebas se prepara esa historia por SQL; **no significa que F1 tenga un endpoint de finalización**.

Cambiar contraseñas en `.env` no cambia automáticamente las de un volumen MySQL ya inicializado. No borrar el volumen como remedio rápido si contiene datos que se quieren conservar. No utilizar `down -v` para una parada habitual.

## 7. Pruebas reproducibles

Unitarias sin MySQL:

```bash
cd backend/recorridos
./mvnw test
```

Integración desde la raíz, con servicio y esquema separados:

```bash
docker --context default compose --env-file infra/.env -f infra/compose.yaml --profile test up -d --wait mysql-test
set -a
. ./infra/.env
set +a
unset MYSQL_ROOT_PASSWORD
cd backend/recorridos
./mvnw verify -Pintegration
```

La suite exige la base `espalearn_test`, por defecto en3308; no usa ni limpia la base principal. `F1_TEST_DB_URL` permite cambiar su dirección conservando ese nombre; si se cambia puerto en `.env`, ajustar también esta URL. Las pruebas no se omiten silenciosamente si falta MySQL. Se puede retirar sólo el contenedor/volumen anónimo de pruebas con `docker --context default compose --env-file infra/.env -f infra/compose.yaml --profile test rm -sfv mysql-test` desde la raíz.

### Resultado de esta ejecución

- **8 unitarias y14 de integración**,0fallos/0errores/0omitidas, ejecutadas con MavenWrapper.
- Integración cruza HTTP real, Spring y MySQL8.4.13, sin H2 ni repositorios falsos.
- Casos: creación/lecturas, ausencia sin alta, propiedad, errores400/409/404, rollback, versiones exactas, reintento, solicitudes nuevas sobre activo, ocho inicios simultáneos, mismo UUID concurrente, restricciónSQL de activo único y exclusión de palabras completadas.
- Ensayo operativo separado: crear un recorrido; detener Spring; **recrear el contenedor MySQL sin borrar volumen**; arrancar un nuevo proceso Spring; comparar JSON completo y reintentarPOST. Mismos ID, palabras, orden y resultado200. Repetido sobre el build final, incluidas las migracionesV1/V2/V3.
- Comprobaciones adicionales del servidor final: campo `palabraIds` enviado por cliente rechazado con400; detener MySQL y consultar el activo produce503 `PERSISTENCIA_NO_DISPONIBLE`, nunca204 ni éxito anticipado.
- Android: assembleDebug/lint correctos (cache reutilizada);114unitarias reejecutadas expresamente con `--rerun-tasks`, todas aprobadas. Lint mantiene0errores/18advertencias del reporte. No se repitieron instrumentadas: no se modificó Android.
- Mockito emite advertencias sobre carga dinámica del agente en Java21; no son fallos, pero conviene adaptar la configuración antes de subir versión deJDK.

Reportes: `backend/recorridos/target/surefire-reports/` y `target/failsafe-reports/`; JAR en `target/`. Son locales/ignorados. No equivalen a cobertura80% ni a una versióncommitteada.

## 8. Cierre y siguiente fase

Los procesos de prueba se detienen al terminar para no dejar servicios de fondo; se conserva el volumen principal y su recorrido de demostración. Se puede volver a arrancar con los comandos anteriores. No se hizo commit/push ni se modificó códigoAndroid.

F1 está cerrado en su alcance mínimo, no el indicador completo de backend/CRUD deEV3. F2 conectará Retrofit/Room/VM/UI y completará preparación y cierres mínimos del recorrido; Groq sigue enF3 y robustezCRUD enF4. La futura eliminación debe resolver solicitudes/historial asociados explícitamente, no desactivar restriccionesSQL. El catálogo editorial y la colaboración evaluada siguen pendientes.
