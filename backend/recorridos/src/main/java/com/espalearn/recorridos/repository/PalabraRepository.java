package com.espalearn.recorridos.repository;

import com.espalearn.recorridos.model.Palabra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/** Función: obtener candidatos de la versión pedida sin repetir palabras ya completadas. */
public interface PalabraRepository extends JpaRepository<Palabra, Long> {
    // Catálogo: permite distinguir una versión inexistente de un pool normal agotado.
    boolean existsByVersionCatalogo(String versionCatalogo);
    List<Palabra> findByVersionCatalogoOrderByPalabraId(String versionCatalogo);
    int countByVersionCatalogo(String versionCatalogo);

    // Selección normal: usa identidad editorial, incluso si una entrada tiene otra versión.
    @Query(value = """
            SELECT p.* FROM palabras p
            WHERE p.version_catalogo = :version
              AND NOT EXISTS (
                SELECT 1 FROM recorrido_palabras rp
                JOIN recorridos r ON r.id = rp.recorrido_id
                JOIN palabras anterior ON anterior.id = rp.palabra_id
                WHERE r.aprendiz_id = :aprendiz AND r.estado = 'COMPLETADO'
                  AND r.tipo = 'NORMAL' AND anterior.palabra_id = p.palabra_id
              )
            ORDER BY p.palabra_id
            """, nativeQuery = true)
    List<Palabra> candidatasNormales(@Param("aprendiz") String aprendizId, @Param("version") String version);
}
