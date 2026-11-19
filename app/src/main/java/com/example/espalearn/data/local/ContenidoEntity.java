package com.example.espalearn.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/** Función: conservar una instantánea editorial por palabra y versión, nunca el avance remoto. */
@Entity(tableName = "contenido", primaryKeys = {"palabraId", "versionCatalogo"})
public class ContenidoEntity {
    // Identidad estable: no se guarda un entero R.drawable ni se sobrescriben otras versiones.
    @NonNull public final String palabraId;
    @NonNull public final String versionCatalogo;
    // Instantánea: JSON validado antes de insertar y nuevamente antes de presentar una carta.
    @NonNull public final String contenidoJson;

    public ContenidoEntity(@NonNull String palabraId, @NonNull String versionCatalogo,
                           @NonNull String contenidoJson) {
        this.palabraId = palabraId;
        this.versionCatalogo = versionCatalogo;
        this.contenidoJson = contenidoJson;
    }
}
