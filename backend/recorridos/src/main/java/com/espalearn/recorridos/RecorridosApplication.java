package com.espalearn.recorridos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Función: arrancar el backend MVC de EspaLearn sin depender del proceso Android. */
@SpringBootApplication
public class RecorridosApplication {
    // Entrada: Spring descubre controladores, servicios, repositorios y configuración.
    public static void main(String[] args) {
        SpringApplication.run(RecorridosApplication.class, args);
    }
}
