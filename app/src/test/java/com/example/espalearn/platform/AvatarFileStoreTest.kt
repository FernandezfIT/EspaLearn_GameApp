package com.example.espalearn.platform

import com.example.espalearn.model.AvatarRef
import java.io.File
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Archivos JVM reales; FileProvider y decodificación Android se prueban en dispositivo. */
class AvatarFileStoreTest {
    @get:Rule
    val temporal = TemporaryFolder()

    private fun store(directorio: File) = AvatarFileStore(directorio, "prueba.fileprovider")

    @Test
    fun `dos intentos no sobrescriben la primera candidata`() {
        val directorio = temporal.newFolder()
        val store = store(directorio)
        val primera = store.crearArchivoCandidato("primera")
        primera.writeText("imagen anterior")
        store.crearArchivoCandidato("segunda")

        assertEquals("imagen anterior", primera.readText())
    }

    @Test(expected = IOException::class)
    fun `un id repetido no trunca el archivo existente`() {
        val store = store(temporal.newFolder())
        store.crearArchivoCandidato("repetido")
        store.crearArchivoCandidato("repetido")
    }

    @Test(expected = IOException::class)
    fun `un directorio no disponible comunica fallo`() {
        store(temporal.newFile()).crearArchivoCandidato("intento")
    }

    @Test
    fun `la limpieza conserva la referencia vigente aunque se llame candidato`() {
        val directorio = temporal.newFolder()
        val store = store(directorio)
        val vigente = store.crearArchivoCandidato("vigente")
        val huerfana = store.crearArchivoCandidato("huerfana")
        val anterior = File(directorio, "avatar_anterior.jpg").apply { writeText("anterior") }
        val otro = File(directorio, "no-es-avatar.txt").apply { writeText("otro") }

        store.limpiarDespuesDelExito(AvatarRef.ArchivoPrivado(vigente.name))

        assertTrue(vigente.exists())
        assertFalse(huerfana.exists())
        assertFalse(anterior.exists())
        assertTrue(otro.exists())
    }

    @Test
    fun `una referencia no permite salir del directorio privado`() {
        val exterior = temporal.newFile("exterior.jpg")
        val store = store(temporal.newFolder())

        assertNull(store.archivoDe(AvatarRef.ArchivoPrivado("../${exterior.name}")))
        assertNull(store.archivoDe(AvatarRef.ArchivoPrivado("..\\${exterior.name}")))
        assertNull(store.archivoDe(AvatarRef.ArchivoPrivado(exterior.absolutePath)))
        assertTrue(exterior.exists())
    }

    @Test(expected = IOException::class)
    fun `confirmar un archivo ausente no devuelve exito ficticio`() {
        val directorio = temporal.newFolder()
        store(directorio).confirmarCandidato(File(directorio, "ausente.jpg"), "nuevo")
    }
}
