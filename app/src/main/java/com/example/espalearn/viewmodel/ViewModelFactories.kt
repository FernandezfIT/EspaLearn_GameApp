package com.example.espalearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Unidad
import com.example.espalearn.repository.AppRepository

/**
 * Fábricas de ViewModels con conexión manual: reciben el repositorio y los
 * argumentos necesarios sin inyección de dependencias. Los ViewModels no
 * guardan Activity, NavController ni launcher de cámara.
 */
object ViewModelFactories {

    fun app(repositorio: AppRepository): ViewModelProvider.Factory =
        factory { AppViewModel(repositorio) }

    fun perfil(repositorio: AppRepository, perfilExistente: Perfil?): ViewModelProvider.Factory =
        factory { PerfilViewModel(repositorio, perfilExistente) }

    fun sesion(
        repositorio: AppRepository,
        etapa: Etapa,
        unidades: List<Unidad>
    ): ViewModelProvider.Factory = factory { SesionViewModel(repositorio, etapa, unidades) }

    private fun <T : ViewModel> factory(crear: () -> T): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = crear() as VM
        }
}
