package com.example.espalearn.ui.mapping

import com.example.espalearn.model.ContenidoUnidades
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/** Comprueba correspondencias, no el renderizado Canvas ni su comprensión educativa. */
class IlustracionUnidadUiTest {
    @Test
    fun `las cuatro unidades tienen dibujos distintos y en el orden esperado`() {
        val dibujos = ContenidoUnidades.todas.map {
            IlustracionUnidadUi.desdeRecurso(it.recursoIlustracion)
        }

        assertEquals(
            listOf(
                IlustracionUnidadUi.LLEGADA,
                IlustracionUnidadUi.DESPEDIDA,
                IlustracionUnidadUi.PIEZA_HORNEADA,
                IlustracionUnidadUi.FRUTA_ROJA
            ),
            dibujos
        )
        assertEquals(4, dibujos.toSet().size)
    }

    @Test
    fun `un recurso ausente o desconocido no se transforma en otra unidad`() {
        assertNull(IlustracionUnidadUi.desdeRecurso(null))
        assertNull(IlustracionUnidadUi.desdeRecurso(""))
        assertNull(IlustracionUnidadUi.desdeRecurso("inexistente"))
    }

    @Test
    fun `las descripciones explican la escena sin dar literalmente la respuesta`() {
        ContenidoUnidades.todas.forEach { unidad ->
            val dibujo = requireNotNull(IlustracionUnidadUi.desdeRecurso(unidad.recursoIlustracion))
            assertFalse(dibujo.descripcion.isBlank())
            assertFalse(dibujo.descripcion.contains(unidad.textoEspanol, ignoreCase = true))
        }
    }
}
