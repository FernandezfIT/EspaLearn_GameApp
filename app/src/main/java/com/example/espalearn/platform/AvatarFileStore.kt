package com.example.espalearn.platform

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.espalearn.model.AvatarRef
import java.io.File

/**
 * Gestión del archivo privado del avatar (K5/B6).
 *
 * La captura se delega mediante un URI de FileProvider con rutas
 * restringidas; no se accede a la cámara directamente ni se piden permisos
 * generalistas. Una foto nueva se guarda como archivo candidato y sólo
 * reemplaza al avatar vigente al confirmar un guardado válido.
 *
 * No existe una transacción única entre el archivo y las preferencias: por
 * eso la referencia candidata se conserva hasta el éxito y el archivo
 * anterior se retira después, dejando la limpieza de huérfanos explícita.
 */
class AvatarFileStore(private val context: Context) {

    /** Nombre estable del archivo candidato del borrador actual. */
    fun crearArchivoCandidato(idBorrador: String): File {
        val directorio = directorioAvatares().apply { mkdirs() }
        return File(directorio, "candidato_$idBorrador.jpg")
    }

    /** URI de FileProvider para delegar la captura sin exponer la ruta. */
    fun uriParaCaptura(archivo: File): Uri =
        FileProvider.getUriForFile(context, authority(), archivo)

    /**
     * Confirma una captura como avatar vigente: mueve el candidato a su
     * nombre definitivo y devuelve su referencia persistible.
     */
    fun confirmarCandidato(archivoCandidato: File, idBorrador: String): AvatarRef.ArchivoPrivado {
        val destino = File(directorioAvatares().apply { mkdirs() }, "avatar_$idBorrador.jpg")
        if (destino.exists()) destino.delete()
        archivoCandidato.renameTo(destino)
        return AvatarRef.ArchivoPrivado(destino.name)
    }

    /** Archivo real de un avatar confirmado, o null si ya no existe. */
    fun archivoDe(avatar: AvatarRef.ArchivoPrivado): File? {
        val archivo = File(directorioAvatares(), avatar.nombreArchivo)
        return archivo.takeIf { it.exists() }
    }

    /**
     * Retira la imagen anterior y los candidatos huérfanos sin tocar la
     * referencia vigente. Se ejecuta después de confirmar el guardado.
     */
    fun limpiarDespuesDelExito(referenciaVigente: AvatarRef.ArchivoPrivado?) {
        val directorio = directorioAvatares()
        if (!directorio.exists()) return

        directorio.listFiles().orEmpty().forEach { archivo ->
            val esCandidatoHuerfano = archivo.name.startsWith("candidato_")
            val esAnterior = referenciaVigente != null && archivo.name != referenciaVigente.nombreArchivo
            if (esCandidatoHuerfano || esAnterior) {
                archivo.delete()
            }
        }
    }

    private fun directorioAvatares(): File = File(context.filesDir, "avatares")

    private fun authority(): String = "${context.packageName}.fileprovider"
}
