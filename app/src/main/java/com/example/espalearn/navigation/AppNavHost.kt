package com.example.espalearn.navigation

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.espalearn.app.AppContainer
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.ContenidoUnidades
import com.example.espalearn.model.state.AppUiState
import com.example.espalearn.model.state.AvatarDraft
import com.example.espalearn.model.state.ModoFormularioPerfil
import com.example.espalearn.platform.AnswerHaptics
import com.example.espalearn.platform.AvatarCapture
import com.example.espalearn.platform.AvatarFileStore
import com.example.espalearn.repository.Resultado
import com.example.espalearn.ui.components.IlustracionVocabulario
import com.example.espalearn.ui.components.rememberAvatarPainter
import com.example.espalearn.ui.mapping.MapeoPresentacion
import com.example.espalearn.ui.screens.arranque.CargandoScreen
import com.example.espalearn.ui.screens.arranque.ErrorCargaScreen
import com.example.espalearn.ui.screens.perfil.PerfilScreen
import com.example.espalearn.ui.screens.resumen.ResumenScreen
import com.example.espalearn.ui.screens.sesion.SesionScreen
import com.example.espalearn.viewmodel.AppViewModel
import com.example.espalearn.viewmodel.PerfilViewModel
import com.example.espalearn.viewmodel.SesionViewModel
import com.example.espalearn.viewmodel.ViewModelFactories
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

/**
 * Rutas reales que conectan los ViewModels de la ruta B con las pantallas ya
 * existentes de la ruta A, sin modificar sus componentes.
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

                is AppUiState.ListoConPerfil -> InicioRoute(
                    estado = estado,
                    prepararEtapa = appViewModel::prepararEtapa,
                    onEtapaPreparada = {
                        navController.navigate(AppDestination.SESION.route) {
                            launchSingleTop = true
                        }
                    },
                    onEditarPerfil = {
                        navController.navigate(AppDestination.PERFIL.route) {
                            launchSingleTop = true
                        }
                    },
                    onVerResumen = {
                        navController.navigate(AppDestination.RESUMEN.route) {
                            launchSingleTop = true
                        }
                    }
                )
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
                marcoAplicado = (estadoApp as? AppUiState.ListoConPerfil)
                    ?.progreso?.marcoAplicado ?: false
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
            val etapa = appViewModel.etapaActivaActual()

            val sesionViewModel: SesionViewModel = viewModel(
                key = "sesion_${etapa.id}",
                factory = ViewModelFactories.sesion(
                    repositorio = appContainer.repositorio,
                    etapa = etapa,
                    unidades = ContenidoUnidades.todas
                )
            )
            val estadoSesion by sesionViewModel.estado.collectAsStateWithLifecycle()
            val finalizada by sesionViewModel.etapaFinalizada.collectAsStateWithLifecycle()
            val vista = LocalView.current
            val haptica = remember(vista, sesionViewModel) { AnswerHaptics(vista) }

            LaunchedEffect(finalizada) {
                if (finalizada) {
                    val primerCierre = sesionViewModel.estado.value.moduloCompletadoPorPrimeraVez
                    sesionViewModel.finalizacionConsumida()
                    if (primerCierre) {
                        navController.navigate(AppDestination.RESUMEN.route) {
                            popUpTo(AppDestination.INICIO.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    } else {
                        navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                    }
                }
            }

            BackHandler {
                if (!estadoSesion.guardandoFinalizacion) {
                    navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                }
            }

            SesionScreen(
                presentacion = MapeoPresentacion.sesionParaPresentacion(estadoSesion),
                onVerAyuda = sesionViewModel::verAyuda,
                onElegir = { opcionId ->
                    val contadorAnterior = sesionViewModel.eleccionesAdmitidas.value
                    sesionViewModel.elegir(opcionId)
                    val contadorActual = sesionViewModel.eleccionesAdmitidas.value
                    // Sólo responde a esta acción admitida, nunca a restaurar estado.
                    if (contadorActual > contadorAnterior) {
                        haptica.notificarEleccionAdmitida(contadorActual)
                    }
                },
                onContinuar = sesionViewModel::continuar,
                ilustracion = {
                    IlustracionVocabulario(recurso = estadoSesion.recursoIlustracion)
                }
            )
        }

        composable(AppDestination.RESUMEN.route) {
            val estadoApp = appState
            val progreso = (estadoApp as? AppUiState.ListoConPerfil)?.progreso
            val nick = (estadoApp as? AppUiState.ListoConPerfil)?.perfil?.nick.orEmpty()
            val guardandoMarco by appViewModel.guardandoMarco.collectAsStateWithLifecycle()
            val errorMarco by appViewModel.errorMarco.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()
            var abriendoEtapa by remember { mutableStateOf(false) }
            var errorApertura by remember { mutableStateOf<String?>(null) }

            BackHandler(enabled = abriendoEtapa || guardandoMarco) { /* Esperar escritura. */ }

            val presentacion = remember(progreso, guardandoMarco, errorMarco, nick) {
                progreso?.let {
                    MapeoPresentacion.resumenParaPresentacion(
                        nick = nick,
                        progreso = it,
                        guardandoMarco = guardandoMarco,
                        errorMarco = errorMarco
                    )
                }
            }

            presentacion?.let {
                ResumenScreen(
                    presentacion = it,
                    onAccionPrincipal = {
                        val etapa = progreso?.etapaParaContinuar
                        if (etapa != null && !abriendoEtapa && !guardandoMarco) {
                            abriendoEtapa = true
                            errorApertura = null
                            scope.launch {
                                try {
                                    when (val resultado = appViewModel.prepararEtapa(etapa)) {
                                        is Resultado.Exito -> navController.navigate(AppDestination.SESION.route) {
                                            // Resumen no queda detrás de la nueva sesión.
                                            popUpTo(AppDestination.INICIO.route) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                        is Resultado.Fallo -> errorApertura = resultado.mensaje
                                    }
                                } finally {
                                    abriendoEtapa = false
                                }
                            }
                        }
                    },
                    onVolverInicio = {
                        navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                    },
                    onCambiarMarco = appViewModel::cambiarMarcoAplicado,
                    abriendoEtapa = abriendoEtapa,
                    errorApertura = errorApertura,
                    avatarPainter = rememberAvatarPainter(
                        (estadoApp as? AppUiState.ListoConPerfil)?.perfil?.avatar
                    )
                )
            }
        }
    }
}
