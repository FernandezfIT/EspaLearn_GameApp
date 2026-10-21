package com.example.espalearn.model.state

/**
 * Modo del formulario de perfil; determina el comportamiento de Atrás.
 */
enum class ModoFormularioPerfil {
    CREAR,
    EDITAR
}

/**
 * Errores de campo del formulario de perfil (K1). Se separan de los fallos
 * de captura o persistencia porque representan problemas distintos.
 */
data class PerfilErrores(
    val nick: String? = null,
    val edad: String? = null
) {
    val hayErrores: Boolean
        get() = nick != null || edad != null
}

/**
 * Estado del formulario de perfil (K1).
 *
 * La edad del borrador se mantiene como texto para representar una entrada
 * vacía o inválida sin darla por guardada; el [Perfil] confirmado usa Int.
 * No se muestran errores al abrir: el primer Guardar activa la validación
 * y, a partir de ahí, se actualizan al corregir.
 */
data class PerfilUiState(
    val modo: ModoFormularioPerfil = ModoFormularioPerfil.CREAR,
    val nick: String = "",
    val edadTexto: String = "",
    val avatarActual: AvatarDraft = AvatarDraft.Predeterminado,
    val avatarCandidato: AvatarDraft? = null,
    val errores: PerfilErrores = PerfilErrores(),
    val validacionActivada: Boolean = false,
    val guardando: Boolean = false,
    val errorCaptura: String? = null,
    val errorGuardado: String? = null
)

/**
 * Referencia de avatar dentro del borrador del formulario: el predeterminado
 * o un archivo candidato aún no confirmado hasta guardar el perfil válido.
 */
sealed interface AvatarDraft {

    data object Predeterminado : AvatarDraft

    data class ArchivoCandidato(val nombreArchivo: String) : AvatarDraft
}
