package com.example.espalearn.ui.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.platform.AvatarFileStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** Lee una miniatura fuera del hilo UI. Archivo ausente/ilegible: avatar geométrico. */
@Composable
fun rememberAvatarPainter(avatar: AvatarRef?): Painter? {
    val context = LocalContext.current.applicationContext
    val store = remember(context) { AvatarFileStore(context) }
    val nombre = (avatar as? AvatarRef.ArchivoPrivado)?.nombreArchivo

    return key(nombre) {
        val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = nombre) {
            value = withContext(Dispatchers.IO) {
                try {
                    val archivo = (avatar as? AvatarRef.ArchivoPrivado)?.let(store::archivoDe)
                    if (archivo == null) {
                        null
                    } else {
                        val dimensiones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeFile(archivo.absolutePath, dimensiones)
                        if (dimensiones.outWidth <= 0 || dimensiones.outHeight <= 0) {
                            null
                        } else {
                            val opciones = BitmapFactory.Options().apply { inSampleSize = 1 }
                            while (dimensiones.outWidth / opciones.inSampleSize > 512 ||
                                dimensiones.outHeight / opciones.inSampleSize > 512
                            ) {
                                opciones.inSampleSize *= 2
                            }
                            BitmapFactory.decodeFile(archivo.absolutePath, opciones)?.asImageBitmap()
                        }
                    }
                } catch (_: IOException) {
                    null
                } catch (_: SecurityException) {
                    null
                }
            }
        }
        remember(imagen) { imagen?.let { BitmapPainter(it) } }
    }
}
