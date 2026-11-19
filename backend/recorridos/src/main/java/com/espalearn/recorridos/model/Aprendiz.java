package com.espalearn.recorridos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Función: conservar una identidad demo y ofrecer una fila bloqueable para sus creaciones. */
@Entity
@Table(name = "aprendices")
public class Aprendiz {
    // Identidad: UUID ficticio, sin nick, edad, foto ni autenticación implícita.
    @Id
    private String id;

    // Persistencia: constructor requerido por JPA; el repositorio asegura la fila con un upsert.
    protected Aprendiz() { }
}
