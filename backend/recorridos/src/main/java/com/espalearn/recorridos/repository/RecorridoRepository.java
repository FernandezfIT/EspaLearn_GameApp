package com.espalearn.recorridos.repository;

import com.espalearn.recorridos.model.Recorrido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Función: recuperar sólo recorridos pertenecientes a la identidad demo solicitada. */
public interface RecorridoRepository extends JpaRepository<Recorrido, String> {
    // Lecturas: no hay acceso público por ID sin comprobar también su propietario.
    Optional<Recorrido> findByAprendizIdAndEstado(String aprendizId, Recorrido.Estado estado);
    Optional<Recorrido> findByIdAndAprendizId(String id, String aprendizId);
    boolean existsByAprendizIdAndEstado(String aprendizId, Recorrido.Estado estado);

    // Cobertura: contar palabras únicas sólo de recorridos normales completados, no etapas.
    @org.springframework.data.jpa.repository.Query(value = """
            SELECT COUNT(DISTINCT p.palabra_id) FROM recorrido_palabras rp
            JOIN recorridos r ON r.id = rp.recorrido_id
            JOIN palabras p ON p.id = rp.palabra_id
            WHERE r.aprendiz_id = :aprendiz AND r.estado = 'COMPLETADO' AND r.tipo = 'NORMAL'
            """, nativeQuery = true)
    int completadasUnicas(@org.springframework.data.repository.query.Param("aprendiz") String aprendiz);
}
