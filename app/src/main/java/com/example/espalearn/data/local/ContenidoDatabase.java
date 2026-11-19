package com.example.espalearn.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/** Función: abrir el almacén estructurado de cartas, separado de DataStore y de MySQL. */
@Database(entities = {ContenidoEntity.class}, version = 1, exportSchema = true)
public abstract class ContenidoDatabase extends RoomDatabase {
    // Acceso: una instancia por proceso desde AppContainer; sin migración destructiva automática.
    public abstract ContenidoDao contenidoDao();
}
