package com.example.espalearn.platform

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** Función: comprobar la protección LAN realmente disponible, sin bloquear imágenes preview incompletas. */
object PermisoRedLocal {
    const val NOMBRE = "android.permission.ACCESS_LOCAL_NETWORK"

    // Compatibilidad: algunos emuladores identificados API37 aún no publican este permiso.
    fun requerido(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 37) return false
        return try {
            context.packageManager.getPermissionInfo(NOMBRE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) { false }
    }

    // Autorización: si el sistema lo implementa, una denegación NO se trata como permiso concedido.
    fun autorizado(context: Context): Boolean = !requerido(context) ||
        ContextCompat.checkSelfPermission(context, NOMBRE) == PackageManager.PERMISSION_GRANTED
}
