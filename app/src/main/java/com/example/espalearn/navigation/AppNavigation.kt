package com.example.espalearn.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: AppDestination,
    perfilContent: @Composable () -> Unit,
    inicioContent: @Composable () -> Unit,
    sesionContent: @Composable () -> Unit,
    resumenContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(AppDestination.PERFIL.route) {
            perfilContent()
        }

        composable(AppDestination.INICIO.route) {
            inicioContent()
        }

        composable(AppDestination.SESION.route) {
            sesionContent()
        }

        composable(AppDestination.RESUMEN.route) {
            resumenContent()
        }
    }
}