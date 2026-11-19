package com.espalearn.recorridos.repository;

import com.espalearn.recorridos.model.SolicitudInicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Función: recuperar el resultado persistido de una solicitud de inicio del mismo aprendiz. */
public interface SolicitudInicioRepository extends JpaRepository<SolicitudInicio, String> {
    // Deduplicación: la combinación está protegida también por una restricción UNIQUE SQL.
    Optional<SolicitudInicio> findByAprendizIdAndSolicitudId(String aprendizId, String solicitudId);
}
