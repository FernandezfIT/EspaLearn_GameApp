package com.example.espalearn.platform

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.espalearn.model.AvatarRef
import java.io.File
import java.io.IOException

/** Archivos privados de avatar. Nunca se borra la referencia persistida vigente. */
class AvatarFileStore internal constructor(
    private val directorio: File,
    private val autoridad: String,
    private val contexto: Context? = null
) {
    constructor(context: Context) : this(
        File(context.filesDir, "avatares"),
        "${context.packageName}.fileprovider",
        context.applicationContext
    )

    /** Cada intento debe recibir un ID nuevo para no sobrescribir una candidata previa. */
    fun crearArchivoCandidato(idBorrador: String): File {
        require(idBorrador.matches(Regex("[A-Za-z0-9-]+")))
        if (!directorio.isDirectory && !directorio.mkdirs()) {
            throw IOException("No se pudo preparar el directorio del avatar.")
        }
        val archivo = File(directorio, "candidato_$idBorrador.jpg")
        if (!archivo.createNewFile()) throw IOException("El archivo de captura ya existe.")
        return archivo
    }

    fun uriParaCaptura(archivo: File): Uri =
        FileProvider.getUriForFile(contextoArchivo(archivo), autoridad, archivo)

    private fun contextoArchivo(archivo: File): Context {
        require(archivo.parentFile?.canonicalFile == directorio.canonicalFile)
        return checkNotNull(contexto) { "Se requiere un contexto Android para FileProvider." }
    }

    /** Movimiento opcional; la Route persiste directamente el nombre privado capturado. */
    fun confirmarCandidato(archivoCandidato: File, idBorrador: String): AvatarRef.ArchivoPrivado {
        require(idBorrador.matches(Regex("[A-Za-z0-9-]+")))
        require(archivoCandidato.parentFile?.canonicalFile == directorio.canonicalFile)
        val destino = File(directorio, "avatar_$idBorrador.jpg")
        if (destino.exists() || !archivoCandidato.renameTo(destino)) {
            throw IOException("No se pudo confirmar el archivo del avatar.")
        }
        return AvatarRef.ArchivoPrivado(destino.name)
    }

    fun archivoDe(avatar: AvatarRef.ArchivoPrivado): File? = archivoPorNombre(avatar.nombreArchivo)

    fun archivoDeUri(uri: Uri): File? {
        if (uri.scheme != "content" || uri.authority != autoridad) return null
        return uri.lastPathSegment?.let(::archivoPorNombre)
    }

    private fun archivoPorNombre(nombre: String): File? {
        if (nombre.isBlank() || nombre != File(nombre).name || '/' in nombre || '\\' in nombre) return null
        val archivo = File(directorio, nombre)
        return archivo.takeIf { it.isFile && it.canonicalFile.parentFile == directorio.canonicalFile }
    }

    fun esImagenValida(archivo: File): Boolean {
        if (!archivo.isFile || archivo.length() == 0L) return false
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(archivo.absolutePath, opciones)
        return opciones.outWidth > 0 && opciones.outHeight > 0
    }

    /** Se llama al guardar con éxito o descartar, no durante una captura activa. */
    fun limpiarDespuesDelExito(referenciaVigente: AvatarRef.ArchivoPrivado?) {
        directorio.listFiles().orEmpty().forEach { archivo ->
            val esAvatar = archivo.name.startsWith("candidato_") || archivo.name.startsWith("avatar_")
            if (esAvatar && archivo.name != referenciaVigente?.nombreArchivo) archivo.delete()
        }
    }
}
