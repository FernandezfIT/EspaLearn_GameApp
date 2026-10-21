package com.example.espalearn.preview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.espalearn.navigation.AppDestination
import com.example.espalearn.navigation.AppNavigation
import com.example.espalearn.ui.screens.inicio.InicioScreen
import com.example.espalearn.ui.screens.perfil.PerfilScreen
import com.example.espalearn.ui.screens.resumen.ResumenScreen
import com.example.espalearn.ui.screens.sesion.SesionScreen
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

@Preview(
    name = "Navegación — demostración",
    widthDp = 360,
    heightDp = 720
)
@Composable
fun NavegacionDemo() {
    val navController = rememberNavController()

    var etapaDemo by rememberSaveable {
        mutableStateOf("demo_con_apoyo")
    }

    var aviso by remember {
        mutableStateOf("DEMO: datos prefijados; no guarda ni completa etapas.")
    }

    fun abrirDesdeInicio(destino: AppDestination) {
        navController.navigate(destino.route) {
            popUpTo(AppDestination.INICIO.route) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    EspaLearn_turco_espaniolTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                Text(
                    text = aviso,
                    modifier = Modifier.padding(16.dp)
                )

                TextButton(
                    onClick = {
                        abrirDesdeInicio(AppDestination.RESUMEN)
                    }
                ) {
                    Text(text = "Abrir Resumen de prueba")
                }

                AppNavigation(
                    navController = navController,
                    startDestination = AppDestination.INICIO,

                    perfilContent = {
                        var nick by remember {
                            mutableStateOf("Explorador")
                        }
                        var edad by remember {
                            mutableStateOf("8")
                        }

                        PerfilScreen(
                            nick = nick,
                            edad = edad,
                            onNickChange = { nick = it },
                            onEdadChange = { edad = it },
                            onTomarFoto = {
                                aviso = "DEMO: cámara solicitada; no se abrió."
                            },
                            onGuardar = {
                                aviso = "DEMO: guardado solicitado; no se guardó."
                            },
                            editando = true
                        )
                    },

                    inicioContent = {
                        InicioScreen(
                            nick = "Explorador",
                            etapas = navEtapasDemo,
                            onEditarPerfil = {
                                navController.navigate(
                                    AppDestination.PERFIL.route
                                ) {
                                    launchSingleTop = true
                                }
                            },
                            onEtapaClick = { id ->
                                if (
                                    id == "demo_descubrir" ||
                                    id == "demo_con_apoyo"
                                ) {
                                    etapaDemo = id
                                    abrirDesdeInicio(AppDestination.SESION)
                                } else {
                                    aviso = "DEMO: escenario no preparado."
                                }
                            }
                        )
                    },

                    sesionContent = {
                        val presentacion = if (
                            etapaDemo == "demo_descubrir"
                        ) {
                            navDescubrirDemo
                        } else {
                            navPracticaDemo
                        }

                        SesionScreen(
                            presentacion = presentacion,
                            onVerAyuda = {
                                aviso = "DEMO: ayuda solicitada."
                            },
                            onElegir = { id ->
                                aviso = "DEMO: elección solicitada $id."
                            },
                            onContinuar = {
                                aviso = "DEMO: Continuar solicitado; sin avance."
                            },
                            ilustracion = {
                                IlustracionNavegacionDemo()
                            }
                        )
                    },

                    resumenContent = {
                        ResumenScreen(
                            presentacion = navResumenDemo,
                            onAccionPrincipal = {
                                etapaDemo = "demo_con_apoyo"
                                abrirDesdeInicio(AppDestination.SESION)
                            },
                            onVolverInicio = {
                                navController.popBackStack(
                                    AppDestination.INICIO.route,
                                    inclusive = false
                                )
                            },
                            onCambiarMarco = { aplicar ->
                                aviso = "DEMO: cambio de marco solicitado: $aplicar."
                            }
                        )
                    },

                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun IlustracionNavegacionDemo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                MaterialTheme.colorScheme.secondaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "[Ilustración de prueba]")
    }
}