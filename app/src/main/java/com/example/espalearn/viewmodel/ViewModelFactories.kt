package com.example.espalearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Unidad
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.VerticalRepository
import com.example.espalearn.repository.SesionPreparada
import com.example.espalearn.repository.Resultado
import com.example.espalearn.domain.ev3.ResultadoPalabra
import com.example.espalearn.model.Progreso

/**
 * Fábricas de ViewModels con conexión manual: reciben el repositorio y los
 * argumentos necesarios sin inyección de dependencias. Los ViewModels no
 * guardan Activity, NavController ni launcher de cámara.
 */
object ViewModelFactories {

    fun app(repositorio: AppRepository): ViewModelProvider.Factory =
        factory { AppViewModel(repositorio) }

    fun perfil(repositorio: AppRepository, perfilExistente: Perfil?): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>, extras: CreationExtras): VM =
                PerfilViewModel(repositorio, perfilExistente, extras.createSavedStateHandle()) as VM
        }

    fun sesion(
        repositorio: AppRepository,
        etapa: Etapa,
        unidades: List<Unidad>
    ): ViewModelProvider.Factory = factory { SesionViewModel(repositorio, etapa, unidades) }

    // F2: identidad de sesión incluye recorrido y etapa; el cierre se inyecta desde la vertical.
    fun recorrido(repositorio: VerticalRepository): ViewModelProvider.Factory = factory { RecorridoViewModel(repositorio) }

    fun sesionEv3(repositorio: AppRepository, preparada: SesionPreparada,
                  confirmar: suspend (List<ResultadoPalabra>) -> Resultado<Progreso>): ViewModelProvider.Factory =
        factory { SesionViewModel(repositorio, preparada.etapa, preparada.unidades,
            "${preparada.recorrido.recorridoId}/${preparada.etapa.id}", confirmar) }

    private fun <T : ViewModel> factory(crear: () -> T): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = crear() as VM
        }
}
