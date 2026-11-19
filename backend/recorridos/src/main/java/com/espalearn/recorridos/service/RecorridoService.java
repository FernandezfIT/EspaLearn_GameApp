package com.espalearn.recorridos.service;

import com.espalearn.recorridos.dto.CrearRecorridoRequest;
import com.espalearn.recorridos.dto.RecorridoResponse;
import com.espalearn.recorridos.model.*;
import com.espalearn.recorridos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/** Función: seleccionar o recuperar un lote coherente y confirmar su persistencia antes de responder. */
@Service
public class RecorridoService {
    // Dependencias: inyectadas para separar reglas, HTTP y acceso a datos, y facilitar pruebas.
    private final AprendizRepository aprendices;
    private final PalabraRepository palabras;
    private final RecorridoRepository recorridos;
    private final SolicitudInicioRepository solicitudes;

    public RecorridoService(AprendizRepository aprendices, PalabraRepository palabras,
                            RecorridoRepository recorridos, SolicitudInicioRepository solicitudes) {
        this.aprendices = aprendices;
        this.palabras = palabras;
        this.recorridos = recorridos;
        this.solicitudes = solicitudes;
    }

    // Resultado interno: diferencia 201 (nuevo) de 200 (recuperado), sin alterar el JSON F0.
    public record Inicio(RecorridoResponse recorrido, boolean creado) { }

    @Transactional(timeout = 15)
    public Inicio crearORecuperar(UUID aprendizId, CrearRecorridoRequest entrada) {
        String aprendiz = aprendizId.toString();
        String solicitud = entrada.solicitudId().toString();

        // Concurrencia: serializar por aprendiz ANTES de comprobar solicitudes o activos.
        aprendices.asegurar(aprendiz);
        aprendices.bloquear(aprendiz).orElseThrow();

        // Idempotencia: recuperar la operación original, incluso después de cerrar el recorrido.
        var previa = solicitudes.findByAprendizIdAndSolicitudId(aprendiz, solicitud);
        if (previa.isPresent()) {
            var registro = previa.get();
            if (registro.getTipo() != entrada.tipo() || !registro.getVersionCatalogo().equals(entrada.versionCatalogo())) {
                throw conflicto("SOLICITUD_INCOMPATIBLE", "La solicitud ya fue utilizada con otro contenido.");
            }
            return new Inicio(obtener(aprendizId, UUID.fromString(registro.getRecorridoId())), false);
        }

        // Activo: un UUID de solicitud nuevo tampoco autoriza sortear otro lote compatible.
        var activo = recorridos.findByAprendizIdAndEstado(aprendiz, Recorrido.Estado.ACTIVO);
        if (activo.isPresent()) {
            Recorrido existente = activo.get();
            if (existente.getTipo() != entrada.tipo() || !existente.getVersionCatalogo().equals(entrada.versionCatalogo())) {
                throw conflicto("ACTIVO_INCOMPATIBLE", "Ya existe un recorrido activo de otro tipo o versión.");
            }
            registrar(aprendiz, solicitud, entrada, existente);
            return new Inicio(RecorridoResponse.desde(existente), false);
        }

        // Alcance F1: no simular repaso/progreso antes de implementar sus reglas completas.
        if (entrada.tipo() == Recorrido.Tipo.REPASO) {
            throw conflicto("REPASO_NO_DISPONIBLE", "El repaso estará disponible al completar el catálogo.");
        }
        if (!palabras.existsByVersionCatalogo(entrada.versionCatalogo())) {
            throw conflicto("CATALOGO_INCOMPATIBLE", "La versión de catálogo solicitada no está disponible.");
        }
        var candidatas = new ArrayList<>(palabras.candidatasNormales(aprendiz, entrada.versionCatalogo()));
        if (candidatas.size() < 3) {
            throw conflicto("PALABRAS_INSUFICIENTES", "No hay tres palabras pendientes para otro recorrido normal.");
        }

        // Selección y escritura: sortear una vez, guardar el orden y asociar la solicitud atómicamente.
        Collections.shuffle(candidatas);
        var nuevo = recorridos.save(new Recorrido(aprendiz, entrada.versionCatalogo(), entrada.tipo(),
                candidatas.subList(0, 3)));
        registrar(aprendiz, solicitud, entrada, nuevo);
        return new Inicio(RecorridoResponse.desde(nuevo), true);
    }

    // Lecturas: no crean identidades, recorridos ni nuevas selecciones por efecto de un GET.
    @Transactional(readOnly = true)
    public Optional<RecorridoResponse> activo(UUID aprendizId) {
        return recorridos.findByAprendizIdAndEstado(aprendizId.toString(), Recorrido.Estado.ACTIVO)
                .map(RecorridoResponse::desde);
    }

    @Transactional(readOnly = true)
    public RecorridoResponse obtener(UUID aprendizId, UUID recorridoId) {
        return recorridos.findByIdAndAprendizId(recorridoId.toString(), aprendizId.toString())
                .map(RecorridoResponse::desde)
                .orElseThrow(() -> new ReglaNegocioException("RECORRIDO_NO_ENCONTRADO",
                        "El recorrido no está disponible para esta identidad.", true));
    }

    // Auxiliares: registrar la misma huella de petición y centralizar los conflictos de negocio.
    private void registrar(String aprendiz, String solicitud, CrearRecorridoRequest entrada, Recorrido recorrido) {
        solicitudes.save(new SolicitudInicio(aprendiz, solicitud, entrada.tipo(),
                entrada.versionCatalogo(), recorrido.getId()));
    }
    private ReglaNegocioException conflicto(String codigo, String mensaje) {
        return new ReglaNegocioException(codigo, mensaje, false);
    }
}
