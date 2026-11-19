package com.espalearn.recorridos.repository;

import com.espalearn.recorridos.model.CierreEtapa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Función: reconciliar cierres por etapa o solicitud sin duplicar efectos remotos. */
public interface CierreEtapaRepository extends JpaRepository<CierreEtapa, String> {
    // Consultas: se ejecutan dentro de la exclusión por aprendiz del servicio.
    Optional<CierreEtapa> findByRecorridoIdAndEtapa(String recorridoId, String etapa);
    Optional<CierreEtapa> findByRecorridoIdAndSolicitudId(String recorridoId, String solicitudId);
}
