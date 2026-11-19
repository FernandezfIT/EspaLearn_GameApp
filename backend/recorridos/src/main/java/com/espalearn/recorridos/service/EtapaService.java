package com.espalearn.recorridos.service;

import com.espalearn.recorridos.dto.*;
import com.espalearn.recorridos.model.*;
import com.espalearn.recorridos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

/** Función: validar y confirmar cada etapa de la vertical Android sin duplicar avance. */
@Service
public class EtapaService {
    // Dependencias: exclusión SQL compartida con los inicios y persistencia del resultado aceptado.
    private final AprendizRepository aprendices;
    private final RecorridoRepository recorridos;
    private final CierreEtapaRepository cierres;
    private final ObjectMapper json;
    private static final List<String> ORDEN = List.of("descubrir", "con_apoyo", "menos_apoyo");

    public EtapaService(AprendizRepository aprendices, RecorridoRepository recorridos,
                        CierreEtapaRepository cierres, ObjectMapper json) {
        this.aprendices = aprendices;
        this.recorridos = recorridos;
        this.cierres = cierres;
        this.json = json;
    }

    @Transactional(timeout = 15)
    public RecorridoResponse confirmar(UUID aprendiz, UUID recorridoId, String etapa, CierreEtapaRequest entrada) {
        // Identidad y versión: una ruta ajena no puede confirmar ni crear datos del recorrido.
        aprendices.bloquear(aprendiz.toString()).orElseThrow(() -> ausencia());
        Recorrido recorrido = recorridos.findByIdAndAprendizId(recorridoId.toString(), aprendiz.toString())
                .orElseThrow(() -> ausencia());
        if (!etapa.equals(entrada.etapa()) || !recorrido.getVersionCatalogo().equals(entrada.versionCatalogo())) {
            throw conflicto("CIERRE_INCOMPATIBLE", "La etapa o versión no corresponde al recorrido.");
        }
        String payload = json.writeValueAsString(List.of(entrada.versionCatalogo(), etapa, entrada.resultados()));
        String huella = huella(payload);

        // Reintentos: resolver lo ya aceptado ANTES de validar la siguiente etapa pendiente.
        var solicitud = cierres.findByRecorridoIdAndSolicitudId(recorridoId.toString(), entrada.solicitudId().toString());
        if (solicitud.isPresent() && (!solicitud.get().getEtapa().equals(etapa) || !solicitud.get().getHuella().equals(huella))) {
            throw conflicto("SOLICITUD_INCOMPATIBLE", "La solicitud ya identifica otro cierre.");
        }
        var previo = cierres.findByRecorridoIdAndEtapa(recorridoId.toString(), etapa);
        if (previo.isPresent()) {
            if (!previo.get().getHuella().equals(huella)
                    || !previo.get().getSolicitudId().equals(entrada.solicitudId().toString())) {
                throw conflicto("CIERRE_INCOMPATIBLE", "La etapa ya fue confirmada con otros resultados.");
            }
            return RecorridoResponse.desde(recorrido);
        }

        // Secuencia y resultados: no aceptar nota/booleano de acierto inventado por el cliente.
        int pendiente = recorrido.getEtapasConfirmadas().size();
        if (recorrido.getEstado() != Recorrido.Estado.ACTIVO || pendiente >= ORDEN.size()
                || !ORDEN.get(pendiente).equals(etapa)) {
            throw conflicto("ETAPA_NO_DISPONIBLE", "Completa primero la etapa pendiente.");
        }
        if (!entrada.resultados().stream().map(CierreEtapaRequest.Resultado::palabraId).toList()
                .equals(recorrido.getPalabras().stream().map(Palabra::getPalabraId).toList())) {
            throw conflicto("LOTE_INCOMPATIBLE", "Los resultados no corresponden al lote y orden guardados.");
        }
        for (int i = 0; i < 3; i++) {
            var resultado = entrada.resultados().get(i);
            var palabra = recorrido.getPalabras().get(i);
            if (!palabra.contenidoCompleto()) {
                throw conflicto("CATALOGO_INCOMPLETO", "La carta aún no tiene contenido completo.");
            }
            boolean descubrir = etapa.equals("descubrir");
            boolean opcionValida = palabra.contenidoCompleto() &&
                    (Objects.equals(resultado.opcionElegidaId(), palabra.getOpcionUnoId()) ||
                     Objects.equals(resultado.opcionElegidaId(), palabra.getOpcionDosId()));
            if ((descubrir && (resultado.opcionElegidaId() != null || resultado.ayudaUsada()))
                    || (!descubrir && !opcionValida)
                    || (!etapa.equals("menos_apoyo") && resultado.ayudaUsada())) {
                throw conflicto("RESULTADO_INVALIDO", "Una respuesta o ayuda no es válida para esta etapa.");
            }
        }

        // Commit: estado y registro del cierre pertenecen a la misma transacción MySQL.
        cierres.save(new CierreEtapa(recorridoId.toString(), etapa, entrada.solicitudId().toString(), huella, payload));
        recorrido.confirmar(etapa);
        return RecorridoResponse.desde(recorrido);
    }

    // Auxiliares: huella de contenido independiente del UUID de petición; mensajes públicos estables.
    private String huella(String contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) { throw new IllegalStateException(error); }
    }
    private ReglaNegocioException conflicto(String codigo, String mensaje) {
        return new ReglaNegocioException(codigo, mensaje, false);
    }
    private ReglaNegocioException ausencia() {
        return new ReglaNegocioException("RECORRIDO_NO_ENCONTRADO", "El recorrido no está disponible para esta identidad.", true);
    }
}
