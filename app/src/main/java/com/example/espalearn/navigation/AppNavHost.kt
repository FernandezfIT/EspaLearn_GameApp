package com.example.espalearn.navigation

import android.net.Uri
import androidx.activity.compose.BackHandler
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
import com.example.espalearn.model.ContenidoUnidades
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.state.AppUiState
import com.example.espalearn.platform.AvatarCapture
import com.example.espalearn.platform.AvatarFileStore
import com.example.espalearn.ui.components.IlustracionPlaceholder
import com.example.espalearn.ui.mapping.MapeoPresentacion
import com.example.espalearn.ui.screens.arranque.CargandoScreen
import com.example.espalearn.ui.screens.arranque.ErrorCargaScreen
import com.example.espalearn.ui.screens.inicio.InicioScreen
import com.example.espalearn.ui.screens.perfil.PerfilScreen
import com.example.espalearn.ui.screens.resumen.ResumenScreen
import com.example.espalearn.ui.screens.sesion.SesionScreen
import com.example.espalearn.viewmodel.AppViewModel
import com.example.espalearn.viewmodel.PerfilViewModel
import com.example.espalearn.viewmodel.SesionViewModel
import com.example.espalearn.viewmodel.ViewModelFactories
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

                is AppUiState.ListoConPerfil -> InicioScreen(
                    nick = estado.perfil.nick,
                    etapas = MapeoPresentacion.etapasParaMapa(
                        progreso = estado.progreso,
                        etapaActiva = estado.progreso.etapaActiva
                    ),
                    onEditarPerfil = {
                        navController.navigate(AppDestination.PERFIL.route) {
                            launchSingleTop = true
                        }
                    },
                    onEtapaClick = { idEtapa ->
                        val etapa = Etapa.desdeId(idEtapa)
                        if (etapa != null) {
                            appViewModel.registrarEtapaActiva(etapa)
                            navController.navigate(AppDestination.SESION.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    avatarPainter = null,
                    marcoAplicado = estado.progreso.marcoAplicado
                )
            }
        }

        composable(AppDestination.PERFIL.route) {
            val estadoApp = appState
            val editando = estadoApp is AppUiState.ListoConPerfil

            val perfilViewModel: PerfilViewModel = viewModel(
                key = if (editando) "perfil_editar" else "perfil_crear",
                factory = ViewModelFactories.perfil(
                    repositorio = appContainer.repositorio,
                    perfilExistente = (estadoApp as? AppUiState.ListoConPerfil)?.perfil
                )
            )
            val estadoPerfil by perfilViewModel.estado.collectAsStateWithLifecycle()
            val guardadoExitoso by perfilViewModel.guardadoExitoso.collectAsStateWithLifecycle()

            val context = LocalContext.current
            val fileStore = remember { AvatarFileStore(context) }
            val capture = remember { AvatarCapture(context, fileStore) }
            val idBorrador = rememberSaveable { UUID.randomUUID().toString() }
            // El URI pendiente se conserva ante recreación para poder recibir el
            // resultado de la cámara y recuperar la candidata (K5/B6).
            var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

            val launcherCaptura = rememberLauncherForActivityResult(
                contract = AvatarCapture.contrato()
            ) { capturoOk ->
                val archivo = uriPendiente?.let(fileStore::archivoDeUri)
                uriPendiente = null
                if (capturoOk && archivo != null) {
                    perfilViewModel.capturaCorrecta(archivo.name)
                } else {
                    // Cancelar no es error y conserva el avatar anterior.
                    perfilViewModel.capturaCancelada()
                }
            }

            // Sólo se sale como guardado exitoso, tras confirmar la escritura.
            LaunchedEffect(guardadoExitoso) {
                if (guardadoExitoso) {
                    perfilViewModel.guardadoConsumido()
                    navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                }
            }

            // Atrás: al crear descarta y sale; al editar vuelve a Inicio.
            // Durante el guardado se bloquea temporalmente la salida.
            BackHandler(enabled = !estadoPerfil.guardando) {
                if (editando) {
                    navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                } else {
                    navController.popBackStack(AppDestination.INICIO.route, inclusive = true)
                }
            }

            PerfilScreen(
                nick = estadoPerfil.nick,
                edad = estadoPerfil.edadTexto,
                onNickChange = perfilViewModel::editarNick,
                onEdadChange = perfilViewModel::editarEdad,
                onTomarFoto = {
                    val preparada = capture.prepararCaptura(idBorrador)
                    uriPendiente = preparada.uri
                    launcherCaptura.launch(preparada.uri)
                },
                onGuardar = perfilViewModel::guardar,
                editando = editando,
                guardando = estadoPerfil.guardando,
                errorNick = estadoPerfil.errores.nick,
                errorEdad = estadoPerfil.errores.edad,
                errorCaptura = estadoPerfil.errorCaptura,
                errorGuardado = estadoPerfil.errorGuardado,
                avatarPainter = null,
                marcoAplicado = (estadoApp as? AppUiState.ListoConPerfil)
                    ?.progreso?.marcoAplicado ?: false
            )
        }

        composable(AppDestination.SESION.route) {
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

            LaunchedEffect(finalizada) {
                if (finalizada) {
                    sesionViewModel.finalizacionConsumida()
                    navController.navigate(AppDestination.RESUMEN.route) {
                        popUpTo(AppDestination.INICIO.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }

            BackHandler(enabled = !estadoSesion.guardandoFinalizacion) {
                navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
            }

            SesionScreen(
                presentacion = MapeoPresentacion.sesionParaPresentacion(estadoSesion),
                onVerAyuda = sesionViewModel::verAyuda,
                onElegir = sesionViewModel::elegir,
                onContinuar = sesionViewModel::continuar,
                ilustracion = {
                    IlustracionPlaceholder(
                        descripcion = estadoSesion.palabraEspanol ?: "vocabulario"
                    )
                }
            )
        }

        composable(AppDestination.RESUMEN.route) {
            val estadoApp = appState
            val progreso = (estadoApp as? AppUiState.ListoConPerfil)?.progreso
            val nick = (estadoApp as? AppUiState.ListoConPerfil)?.perfil?.nick.orEmpty()
            val guardandoMarco by appViewModel.guardandoMarco.collectAsStateWithLifecycle()
            val errorMarco by appViewModel.errorMarco.collectAsStateWithLifecycle()

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
                        navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                    },
                    onVolverInicio = {
                        navController.popBackStack(AppDestination.INICIO.route, inclusive = false)
                    },
                    onCambiarMarco = appViewModel::cambiarMarcoAplicado
                )
            }
        }
    }
}
