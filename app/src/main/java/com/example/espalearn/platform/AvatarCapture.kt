package com.example.espalearn.platform

import android.content.Context
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.espalearn.model.AvatarRef
import java.io.File

/**
 * Coordinación de la captura delegada del avatar (K5/B6).
 *
 * Usa [ActivityResultContracts.TakePicture] con un URI de FileProvider: la
 * cámara se delega, sin acceso directo ni permisos generalistas. El archivo
 * candidato se prepara antes de lanzar; la cancelación no se trata como
 * error y un fallo se comunica para permitir reintentar conservando el
 * avatar anterior.
 *
 * Esta clase no es un ViewModel y no forma parte del estado: sólo prepara
 * archivos y traduce el resultado en referencias que el ViewModel consume.
 */
class AvatarCapture(
    private val context: Context,
    private val fileStore: AvatarFileStore
) {

    /** Prepara un archivo candidato y el URI con permiso de escritura para la cámara. */
    fun prepararCaptura(idBorrador: String): Captura {
        val archivo = fileStore.crearArchivoCandidato(idBorrador)
        val uri = fileStore.uriParaCaptura(archivo)
        return Captura(archivo, uri)
    }

    /** Concede el permiso de escritura del URI a la app de cámara que responda. */
    fun concederPermiso(uri: Uri, paqueteCamara: String?) {
        paqueteCamara ?: return
        context.grantUriPermission(
            paqueteCamara,
            uri,
            android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    /**
     * Interpreta el resultado de la captura.
     *
     * [exito] es true cuando la cámara escribió la imagen. Un resultado falso
     * significa cancelación: no es error y no modifica el avatar anterior.
     */
    fun interpretarResultado(captura: Captura, exito: Boolean): ResultadoCaptura =
        if (exito) {
            ResultadoCaptura.Capturada(captura.archivo)
        } else {
            ResultadoCaptura.Cancelada
        }

    /** Confirma la candidata como avatar vigente y devuelve su referencia. */
    fun confirmar(archivoCandidato: File, idBorrador: String): AvatarRef.ArchivoPrivado =
        fileStore.confirmarCandidato(archivoCandidato, idBorrador)

    /** Archivo a fotografiar y su URI de FileProvider. */
    data class Captura(val archivo: File, val uri: Uri)

    sealed interface ResultadoCaptura {
        data class Capturada(val archivo: File) : ResultadoCaptura
        data object Cancelada : ResultadoCaptura
    }

    companion object {
        /** Contrato de captura de una imagen a un URI dado. */
        fun contrato(): ActivityResultContracts.TakePicture = ActivityResultContracts.TakePicture()
    }
}
