package com.example.espalearn.data.local;

import androidx.room.*;
import java.util.List;

/** Función: leer y guardar sólo las cartas asignadas; no autorizar cierres de etapas. */
@Dao
public interface ContenidoDao {
    // Lectura: la capa repositorio reconstruye el orden persistido en el recorrido.
    @Query("SELECT * FROM contenido WHERE versionCatalogo = :version AND palabraId IN (:ids)")
    List<ContenidoEntity> leer(String version, List<String> ids);

    // Escritura atómica: conservar todas las cartas preparadas o ninguna ante un fallo SQL.
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void guardar(List<ContenidoEntity> cartas);
}
