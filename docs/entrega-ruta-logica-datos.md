# Entrega de la ruta de lógica y datos — handoff para la ruta de interfaz

[Alcance](alcance-y-decisiones.md) · [Contratos](contratos.md) · [Ruta B](ruta-b-claudio.md) · [Checklist](tareas-y-dependencias.md) · [Pruebas](pruebas.md)

**Fecha: 20/10/2026.** Rama `feature/logica-datos`, creada desde `feature/base-ui`. Este documento describe qué está listo para que la ruta de interfaz cierre su parte, cómo consumirlo y qué queda pendiente. No sustituye al PR ni a la revisión del otro integrante.

> **Actualización de integración:** este handoff se conserva como entrega histórica de B. El PR #2 ya se integró en develop `427f1ef`; los pasos que piden abrirlo más abajo quedaron superados. El cierre local de Felipe corrige/conecta háptica, avatar, restauración, contenedor, dibujos y navegación; ver README y pruebas.md. No se atribuye revisión de Claudio de esos cambios aún sin publicar.

## 1. Qué se entrega

- Modelos de dominio y estados de interfaz (contratos K0–K4).
- Repositorio único `AppRepository` con `Resultado` y un repositorio falso para pruebas.
- Validación centralizada de nick y edad (`domain/PerfilValidator`).
- ViewModels (`App`, `Perfil`, `Sesion`) con factories manuales.
- Persistencia local (`AppPreferencesStore` sobre DataStore, `LocalAppRepository`, `AppContainer`).
- Háptica (`platform/AnswerHaptics`) y cámara (`platform/AvatarFileStore`, `platform/AvatarCapture`) conectadas a la lógica.
- Catálogo real de cuatro unidades (`model/ContenidoUnidades`) con ilustración provisional.
- Integración de navegación (`navigation/AppNavHost`) y `MainActivity` con el contenedor real.

## 2. Cómo está conectado (y cómo reproduce el flujo)

- `MainActivity` crea un `AppContainer(applicationContext)` y monta `AppNavHost`.
- `AppNavHost` observa `AppViewModel` a nivel de Activity y traduce el estado con `ui/mapping/MapeoPresentacion` hacia las pantallas de la ruta de interfaz, **sin modificar sus `Screen` ni sus componentes**.
- Arranque: `CargandoScreen` → si hay error, `ErrorCargaScreen` con reintento → sin perfil abre Perfil → con perfil muestra Inicio.
- Perfil: la captura usa `ActivityResultContracts.TakePicture` con URI de FileProvider; el resultado se guarda como candidata y sólo se confirma al guardar un perfil válido.
- Sesión: recibe la etapa activa y las unidades del catálogo; deduplica elecciones por sesión/paso; al finalizar persiste y navega a Resumen.
- Resumen: muestra el logro y el control de marco sólo cuando corresponde.

## 3. Cómo consumirlo desde la interfaz

- **No** crear un repositorio, validador ni ViewModel paralelo: usar los de `feature/logica-datos`.
- La interfaz representa estado y emite acciones; no persiste ni valida por su cuenta.
- Para reutilizar el catálogo: `ContenidoUnidades.todas` (ids `hola`, `adios`, `pan`, `manzana`).
- Para sustituir la ilustración provisional: ver la sección "Cómo sustituir los marcadores de posición" en [contenido-y-licencias.md](contenido-y-licencias.md). El punto de reemplazo está en la Route de Sesión, donde hoy se dibuja `IlustracionPlaceholder`.
- Si un cambio requiere modificar la firma de una `Screen`, acordarlo antes de tocar la pantalla.

## 4. Qué falta y de quién depende

| Pendiente | Depende de | Nota |
|---|---|---|
| PR `feature/logica-datos` → `develop` y revisión | Revisión del otro integrante | No autoaprobar. |
| Verificación física de cámara y háptica | Dispositivo con cámara | Casos P15–P19 en pruebas.md. |
| Rotación y muerte de proceso | Dispositivo | La etapa activa se pierde al recrear el proceso. |
| Persistencia DataStore real | Dispositivo | La suite usa almacén en memoria. |
| Ilustraciones autorizadas y traducciones finales | Contenido | Hoy hay marcador de posición explícito. |
| Confirmación del rango de edad 7–10 | Docente | No presentar como cumplimiento literal. |

## 5. Evidencia de esta entrega

- Compilación: `:app:compileDebugKotlin` y `:app:assembleDebug` con éxito.
- Pruebas unitarias: 57 pruebas, 0 fallos. Detalle en [pruebas.md](pruebas.md).
- Lo no probado se declara como pendiente; no se atribuyen resultados físicos ni pedagógicos.

## 6. Enlace de PR

Abrir en el navegador (la herramienta `gh` no está instalada):

```
https://github.com/FernandezfIT/EspaLearn_GameApp/pull/new/feature/logica-datos
```

Destino: `develop`. Comparar contra `develop`, no contra `feature/base-ui`.
