package com.espalearn.recorridos.service;

import com.espalearn.recorridos.dto.CrearRecorridoRequest;
import com.espalearn.recorridos.model.*;
import com.espalearn.recorridos.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Función: probar decisiones del servicio con dobles, sin atribuirles garantías SQL reales. */
@ExtendWith(MockitoExtension.class)
class RecorridoServiceTest {
    // Preparación: dependencias simuladas y una identidad distinta de cualquier perfil Android.
    @Mock AprendizRepository aprendices;
    @Mock PalabraRepository palabras;
    @Mock RecorridoRepository recorridos;
    @Mock SolicitudInicioRepository solicitudes;
    @InjectMocks RecorridoService servicio;
    private final UUID aprendiz = UUID.randomUUID();
    private final CrearRecorridoRequest entrada = new CrearRecorridoRequest(UUID.randomUUID(), Recorrido.Tipo.NORMAL, "prueba-3-v1");

    @BeforeEach void identidadDisponible() {
        when(aprendices.bloquear(aprendiz.toString())).thenReturn(Optional.of(mock(Aprendiz.class)));
    }

    // Fixtures: palabras simuladas; no constituyen catálogo editorial ni entidades guardadas.
    private List<Palabra> lote() {
        return List.of("pan", "manzana", "pera").stream().map(id -> {
            Palabra palabra = mock(Palabra.class);
            when(palabra.getPalabraId()).thenReturn(id);
            return palabra;
        }).toList();
    }
    private Recorrido existente() { return new Recorrido(aprendiz.toString(), "prueba-3-v1", Recorrido.Tipo.NORMAL, lote()); }

    // Creación: exige catálogo suficiente, registra solicitud y devuelve tres IDs únicos.
    @Test void creaLoteYRegistraLaSolicitud() {
        when(palabras.existsByVersionCatalogo(entrada.versionCatalogo())).thenReturn(true);
        var candidatas = lote();
        when(palabras.candidatasNormales(anyString(), anyString())).thenReturn(candidatas);
        when(recorridos.save(any())).thenAnswer(i -> i.getArgument(0));
        var resultado = servicio.crearORecuperar(aprendiz, entrada);
        assertTrue(resultado.creado());
        assertEquals(Set.of("pan", "manzana", "pera"), new HashSet<>(resultado.recorrido().palabraIds()));
        verify(solicitudes).save(any(SolicitudInicio.class));
        var orden = inOrder(aprendices, solicitudes);
        orden.verify(aprendices).asegurar(aprendiz.toString());
        orden.verify(aprendices).bloquear(aprendiz.toString());
        orden.verify(solicitudes).findByAprendizIdAndSolicitudId(anyString(), anyString());
    }

    // Recuperación: conservar lote y detectar cambios de contenido en una solicitud aceptada.
    @Test void nuevaSolicitudRecuperaActivoSinConsultarCatalogo() {
        var activo = existente();
        when(recorridos.findByAprendizIdAndEstado(anyString(), any())).thenReturn(Optional.of(activo));
        var resultado = servicio.crearORecuperar(aprendiz, entrada);
        assertFalse(resultado.creado());
        assertEquals(activo.getId(), resultado.recorrido().recorridoId().toString());
        verifyNoInteractions(palabras);
        verify(recorridos, never()).save(any());
    }
    @Test void mismaSolicitudDevuelveResultadoOriginal() {
        var activo = existente();
        when(solicitudes.findByAprendizIdAndSolicitudId(anyString(), anyString())).thenReturn(Optional.of(
                new SolicitudInicio(aprendiz.toString(), entrada.solicitudId().toString(), entrada.tipo(), entrada.versionCatalogo(), activo.getId())));
        when(recorridos.findByIdAndAprendizId(activo.getId(), aprendiz.toString())).thenReturn(Optional.of(activo));
        assertFalse(servicio.crearORecuperar(aprendiz, entrada).creado());
        verifyNoInteractions(palabras);
        verify(solicitudes, never()).save(any());
    }
    @Test void solicitudReutilizadaConOtraVersionEsConflicto() {
        when(solicitudes.findByAprendizIdAndSolicitudId(anyString(), anyString())).thenReturn(Optional.of(
                new SolicitudInicio(aprendiz.toString(), entrada.solicitudId().toString(), entrada.tipo(), "otra", UUID.randomUUID().toString())));
        assertEquals("SOLICITUD_INCOMPATIBLE", assertThrows(ReglaNegocioException.class,
                () -> servicio.crearORecuperar(aprendiz, entrada)).getCodigo());
        verifyNoInteractions(palabras);
    }
    @Test void activoDeOtraVersionNoSeReemplaza() {
        var activo = existente();
        when(recorridos.findByAprendizIdAndEstado(anyString(), any())).thenReturn(Optional.of(activo));
        var otra = new CrearRecorridoRequest(UUID.randomUUID(), Recorrido.Tipo.NORMAL, "otra");
        assertEquals("ACTIVO_INCOMPATIBLE", assertThrows(ReglaNegocioException.class,
                () -> servicio.crearORecuperar(aprendiz, otra)).getCodigo());
    }

    // Rechazos: no crear recorridos ficticios ante repaso no habilitado o catálogo inválido.
    @Test void versionDesconocidaNoGuardaRecorrido() {
        assertEquals("CATALOGO_INCOMPATIBLE", assertThrows(ReglaNegocioException.class,
                () -> servicio.crearORecuperar(aprendiz, entrada)).getCodigo());
        verify(recorridos, never()).save(any());
    }
    @Test void menosDeTresPendientesNoCreaLote() {
        when(palabras.existsByVersionCatalogo(anyString())).thenReturn(true);
        when(palabras.candidatasNormales(anyString(), anyString())).thenReturn(List.of());
        assertEquals("PALABRAS_INSUFICIENTES", assertThrows(ReglaNegocioException.class,
                () -> servicio.crearORecuperar(aprendiz, entrada)).getCodigo());
    }
    @Test void repasoNoSeSimulaEnF1() {
        assertEquals("REPASO_NO_DISPONIBLE", assertThrows(ReglaNegocioException.class,
                () -> servicio.crearORecuperar(aprendiz, new CrearRecorridoRequest(UUID.randomUUID(), Recorrido.Tipo.REPASO, "prueba-3-v1"))).getCodigo());
    }
}
