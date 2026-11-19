package com.example.espalearn.navigation

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.espalearn.platform.PermisoRedLocal
import com.example.espalearn.ui.screens.recorrido.RecorridoScreen
import com.example.espalearn.viewmodel.RecorridoViewModel
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.espalearn.app.AppContainer
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.state.AppUiState
import com.example.espalearn.model.state.AvatarDraft
import com.example.espalearn.model.state.ModoFormularioPerfil
import com.example.espalearn.platform.AvatarCapture
import com.example.espalearn.platform.AvatarFileStore
import com.example.espalearn.ui.screens.arranque.CargandoScreen
import com.example.espalearn.ui.screens.arranque.ErrorCargaScreen
import com.example.espalearn.ui.components.rememberAvatarPainter
import com.example.espalearn.ui.screens.perfil.PerfilScreen
import com.example.espalearn.viewmodel.AppViewModel
import com.example.espalearn.viewmodel.PerfilViewModel
import com.example.espalearn.viewmodel.ViewModelFactories
import java.io.IOException
import java.util.UUID

/**
 * Función: coordinar perfil local y recorrido EV3 remoto sin mezclar sus autoridades.
 * La sesión reutiliza sus componentes; carga/error no se interpretan como perfil ausente.
 *
 * El [AppViewModel] se comparte a nivel de Activity para que el estado global
 * no se duplique. Perfil y Sesión usan los ViewModels de su destino. La
 * captura de cámara se delega con un URI de FileProvider y el resultado se
 * traduce en un borrador que sólo se confirma al guardar un perfil válido.
 */
@Composable
fun AppNavHost(
    appContainer: AppContainer,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val appViewModel: AppViewModel = viewModel(
        factory = ViewModelFactories.app(appContainer.repositorio)
    )
    val appState by appViewModel.estadoAplicacion.collectAsStateWithLifecycle()
    // EV3: estado remoto separado del perfil local; una sola instancia por Activity.
    val recorrido: RecorridoViewModel = viewModel(factory = ViewModelFactories.recorrido(appContainer.vertical))
    val estadoRecorrido by recorrido.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    val permisoLan = PermisoRedLocal.NOMBRE
    var redLocal by remember { mutableStateOf(PermisoRedLocal.autorizado(contexto)) }
    val pedirRed = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { redLocal = it }
    // Revalidar al volver de Ajustes: no conservar un permiso revocado en un booleano antiguo.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { redLocal = PermisoRedLocal.autorizado(contexto) }
    LaunchedEffect(appState is AppUiState.ListoConPerfil, redLocal) {
        if (appState is AppUiState.ListoConPerfil && redLocal) recorrido.recargar()
    }

    NavHost(
        navController = navController,
        startDestination = AppDestination.INICIO.route,
        modifier = modifier
    ) {
        composable(AppDestination.INICIO.route) {
            when (val estado = appState) {
                is AppUiState.Cargando -> CargandoScreen()

                is AppUiState.ErrorLectura -> ErrorCargaScreen(
                    mensaje = estado.mensaje,
                    onReintentar = appViewModel::recargar
                )

                is AppUiState.ListoSinPerfil -> {
                    // Sin perfil: se abre el formulario de creación una vez.
                    LaunchedEffect(Unit) {
                        navController.navigate(AppDestination.PERFIL.route) {
                            launchSingleTop = true
                        }
                    }
                }

                is AppUiState.ListoConPerfil -> {
                    LaunchedEffect(Unit) { if (redLocal) recorrido.recargar() }
                    RecorridoScreen(estado.perfil, estadoRecorrido, resumen = false,
                        puedeConectar = redLocal, marcoLegadoAplicado = estado.progreso.marcoAplicado,
                        onPermiso = { pedirRed.launch(permisoLan) },
                        onAbrir = { recorrido.abrir { navController.navigate(AppDestination.SESION.route) { launchSingleTop = true } } },
                        onActualizar = recorrido::recargar,
                        onPerfil = { navController.navigate(AppDestination.PERFIL.route) { launchSingleTop = true } },
                        onResumen = { navController.navigate(AppDestination.RESUMEN.route) { launchSingleTop = true } },
                        onVolver = {}, onMarco = recorrido::cambiarMarco)
                }
            }
        }

        composable(AppDestination.PERFIL.route) {
            val estadoApp = appState
            // Tras muerte de proceso, esperar la lectura antes de crear el formulario.
            when (estadoApp) {
                AppUiState.Cargando -> {
                    CargandoScreen()
                    return@composable
                }
                is AppUiState.ErrorLectura -> {
                    ErrorCargaScreen(estadoApp.mensaje, appViewModel::recargar)
                    return@composable
                }
                else -> Unit
            }

            val perfilViewModel: PerfilViewModel = viewModel(
                // Identidad estable: guardar el primer perfil no debe crear otro ViewModel.
                key = "perfil",
                factory = ViewModelFactories.perfil(
                    repositorio = appContainer.repositorio,
                    perfilExistente = (estadoApp as? AppUiState.ListoConPerfil)?.perfil
                )
            )
            val estadoPerfil by perfilViewModel.estado.collectAsStateWithLifecycle()
            val editando = estadoPerfil.modo == ModoFormularioPerfil.EDITAR
            val avatarBorrador = estadoPerfil.avatarCandidato ?: estadoPerfil.avatarActual
            val avatarRef = (avatarBorrador as? AvatarDraft.ArchivoCandidato)?.let {
                AvatarRef.ArchivoPrivado(it.nombreArchivo)
            }
            val guardadoExitoso by perfilViewModel.guardadoExitoso.collectAsStateWithLifecycle()

            val context = LocalContext.current
            val activity = LocalActivity.current
            val fileStore = remember { AvatarFileStore(context) }
            val capture = remember { AvatarCapture(context, fileStore) }
            // El URI pendiente se conserva ante recreación para poder recibir el
            // resultado de la cámara y recuperar la candidata (K5/B6).
            var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

            fun falloCaptura(mensaje: String) {
                uriPendiente?.let(fileStore::archivoDeUri)?.delete()
                uriPendiente = null
                perfilViewModel.capturaFallida(mensaje)
            }

            val launcherCaptura = rememberLauncherForActivityResult(
                contract = AvatarCapture.contrato()
            ) { capturoOk ->
                val archivo = uriPendiente?.let(fileStore::archivoDeUri)
                uriPendiente = null
                when {
                    !capturoOk -> {
                        archivo?.delete()
                        perfilViewModel.capturaCancelada()
                    }
                    archivo != null && fileStore.esImagenValida(archivo) -> {
                        perfilViewModel.capturaCorrecta(archivo.name)
                    }
                    else -> {
                        archivo?.delete()
                        perfilViewModel.capturaFallida("La cámara no produjo una imagen válida. Intenta otra vez.")
                    }
                }
            }

            // Sólo se sale como guardado exitoso, tras confirmar la escritura.
            LaunchedEffect(guardadoExitoso) {
                if (guardadoExitoso) {
                    fileStore.limpiarDespuesDelExito(
                        appViewModel.perfilActual()?.avatar as? AvatarRef.ArchivoPrivado
                    )
                    perfilViewModel.guardadoConsumido()
                    navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                }
            }

            // Atrás: al crear descarta y sale; al editar vuelve a Inicio.
            // Durante el guardado se bloquea temporalmente la salida.
            BackHandler {
                if (!estadoPerfil.guardando && uriPendiente == null) {
                    fileStore.limpiarDespuesDelExito(
                        appViewModel.perfilActual()?.avatar as? AvatarRef.ArchivoPrivado
                    )
                    if (editando) {
                        navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                    } else {
                        activity?.finish()
                    }
                }
            }

            PerfilScreen(
                nick = estadoPerfil.nick,
                edad = estadoPerfil.edadTexto,
                onNickChange = perfilViewModel::editarNick,
                onEdadChange = perfilViewModel::editarEdad,
                onTomarFoto = {
                    if (uriPendiente == null && !estadoPerfil.guardando) {
                        try {
                            val preparada = capture.prepararCaptura(UUID.randomUUID().toString())
                            uriPendiente = preparada.uri
                            launcherCaptura.launch(preparada.uri)
                        } catch (_: ActivityNotFoundException) {
                            falloCaptura("No hay una aplicación de cámara disponible.")
                        } catch (_: IOException) {
                            falloCaptura("No se pudo preparar el archivo. Intenta otra vez.")
                        } catch (_: SecurityException) {
                            falloCaptura("No se pudo dar acceso a la cámara. Intenta otra vez.")
                        } catch (_: IllegalArgumentException) {
                            falloCaptura("No se pudo preparar la captura. Intenta otra vez.")
                        }
                    }
                },
                onGuardar = perfilViewModel::guardar,
                editando = editando,
                guardando = estadoPerfil.guardando || uriPendiente != null,
                errorNick = estadoPerfil.errores.nick,
                errorEdad = estadoPerfil.errores.edad,
                errorCaptura = estadoPerfil.errorCaptura,
                errorGuardado = estadoPerfil.errorGuardado,
                avatarPainter = rememberAvatarPainter(avatarRef),
                marcoAplicado = estadoRecorrido.info?.marcoAplicado ?: ((estadoApp as? AppUiState.ListoConPerfil)
                    ?.progreso?.marcoAplicado ?: false)
            )
        }

        composable(AppDestination.SESION.route) {
            // La etapa persistida no está disponible hasta terminar la lectura inicial.
            when (val estadoApp = appState) {
                AppUiState.Cargando -> {
                    CargandoScreen()
                    return@composable
                }
                is AppUiState.ErrorLectura -> {
                    ErrorCargaScreen(estadoApp.mensaje, appViewModel::recargar)
                    return@composable
                }
                AppUiState.ListoSinPerfil -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                    }
                    return@composable
                }
                is AppUiState.ListoConPerfil -> Unit
            }
            // Restauración de proceso: sin cartas preparadas, volver a Inicio y consultar el activo.
            val preparada = estadoRecorrido.preparada
            if (preparada == null) {
                LaunchedEffect(Unit) { navController.popBackStack(AppDestination.INICIO.route, false) }
                CargandoScreen()
            } else {
                SesionRecorridoRoute(appContainer, recorrido, preparada,
                    onInicio = { navController.popBackStack(AppDestination.INICIO.route, false) },
                    onResumen = {
                        navController.navigate(AppDestination.RESUMEN.route) {
                            popUpTo(AppDestination.INICIO.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    })
            }
        }

        composable(AppDestination.RESUMEN.route) {
            val estadoApp = appState as? AppUiState.ListoConPerfil
            LaunchedEffect(Unit) { if (redLocal) recorrido.recargar() }
            if (estadoApp == null) CargandoScreen() else {
                RecorridoScreen(estadoApp.perfil, estadoRecorrido, resumen = true,
                    puedeConectar = redLocal, marcoLegadoAplicado = estadoApp.progreso.marcoAplicado,
                    onPermiso = { pedirRed.launch(permisoLan) },
                    onAbrir = { recorrido.abrir { navController.navigate(AppDestination.SESION.route) {
                        popUpTo(AppDestination.INICIO.route) { inclusive = false }; launchSingleTop = true
                    } } },
                    onActualizar = recorrido::recargar,
                    onPerfil = { navController.navigate(AppDestination.PERFIL.route) { launchSingleTop = true } },
                    onResumen = {},
                    onVolver = { navController.popBackStack(AppDestination.INICIO.route, false) },
                    onMarco = recorrido::cambiarMarco)
            }
        }
    }
}
