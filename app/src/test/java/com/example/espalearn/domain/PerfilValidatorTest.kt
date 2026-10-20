package com.example.espalearn.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PerfilValidatorTest {

    // --- Nick: límites de longitud en puntos de código ---

    @Test
    fun `nick de 3 puntos de codigo es valido`() {
        assertNull(PerfilValidator.validarNick("ana"))
    }

    @Test
    fun `nick de 20 puntos de codigo es valido`() {
        assertNull(PerfilValidator.validarNick("a".repeat(20)))
    }

    @Test
    fun `nick de 2 puntos de codigo es rechazado`() {
        assertNotNull(PerfilValidator.validarNick("ab"))
    }

    @Test
    fun `nick de 21 puntos de codigo es rechazado`() {
        assertNotNull(PerfilValidator.validarNick("a".repeat(21)))
    }

    // --- Nick: caracteres permitidos y excluidos ---

    @Test
    fun `nick vacio es rechazado`() {
        assertNotNull(PerfilValidator.validarNick(""))
    }

    @Test
    fun `nick con espacio es rechazado`() {
        assertNotNull(PerfilValidator.validarNick("an a"))
    }

    @Test
    fun `nick con guion es rechazado`() {
        assertNotNull(PerfilValidator.validarNick("an-a"))
    }

    @Test
    fun `nick con emoji es rechazado`() {
        assertNotNull(PerfilValidator.validarNick("ana\uD83D\uDE00"))
    }

    @Test
    fun `nick solo numerico es valido`() {
        assertNull(PerfilValidator.validarNick("12345"))
    }

    @Test
    fun `nick con letras espanolas es valido`() {
        assertNull(PerfilValidator.validarNick("ñandú"))
    }

    @Test
    fun `nick con letras turcas es valido`() {
        assertNull(PerfilValidator.validarNick("Şükrü"))
    }

    @Test
    fun `nick con digito no ascii es rechazado`() {
        // ٣ es un dígito árabe-índico: es dígito, pero no ASCII 0-9.
        assertNotNull(PerfilValidator.validarNick("a٣b"))
    }

    // --- Nick: normalización NFC ---

    @Test
    fun `nick se normaliza con NFC`() {
        val descompuesto = "n\u0303andu" // n + tilde combinante
        assertEquals("ñandu", PerfilValidator.normalizarNick(descompuesto))
        assertNull(PerfilValidator.validarNick(descompuesto))
    }

    // --- Edad simulada ---

    @Test
    fun `edad 7 es valida`() {
        assertNull(PerfilValidator.validarEdad("7"))
    }

    @Test
    fun `edad 10 es valida`() {
        assertNull(PerfilValidator.validarEdad("10"))
    }

    @Test
    fun `edad 6 es rechazada`() {
        assertNotNull(PerfilValidator.validarEdad("6"))
    }

    @Test
    fun `edad 11 es rechazada`() {
        assertNotNull(PerfilValidator.validarEdad("11"))
    }

    @Test
    fun `edad vacia es rechazada`() {
        assertNotNull(PerfilValidator.validarEdad(""))
    }

    @Test
    fun `edad de texto es rechazada`() {
        assertNotNull(PerfilValidator.validarEdad("ocho"))
    }

    @Test
    fun `edad fraccionaria es rechazada`() {
        assertNotNull(PerfilValidator.validarEdad("7.5"))
    }

    @Test
    fun `edad confirmada devuelve el entero`() {
        assertEquals(8, PerfilValidator.edadConfirmada("8"))
        assertNull(PerfilValidator.edadConfirmada("12"))
    }

    // --- Validación completa ---

    @Test
    fun `validacion completa expone ambos errores`() {
        val errores = PerfilValidator.validar("ab", "12")
        assertNotNull(errores.nick)
        assertNotNull(errores.edad)
    }

    @Test
    fun `validacion completa sin errores`() {
        val errores = PerfilValidator.validar("Deniz123", "8")
        assertNull(errores.nick)
        assertNull(errores.edad)
        assertEquals(false, errores.hayErrores)
    }
}
