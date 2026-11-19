package com.espalearn.recorridos.repository;

import com.espalearn.recorridos.model.Aprendiz;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/** Función: coordinar creaciones de un mismo aprendiz mediante una fila real de MySQL. */
public interface AprendizRepository extends JpaRepository<Aprendiz, String> {
    // Alta idempotente: el upsert también coordina dos primeras solicitudes simultáneas.
    @Modifying
    @Query(value = "INSERT INTO aprendices(id) VALUES (:id) ON DUPLICATE KEY UPDATE id = id", nativeQuery = true)
    void asegurar(@Param("id") String id);

    // Exclusión: ninguna segunda creación del aprendiz pasa hasta el commit de la primera.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aprendiz a where a.id = :id")
    Optional<Aprendiz> bloquear(@Param("id") String id);
}
