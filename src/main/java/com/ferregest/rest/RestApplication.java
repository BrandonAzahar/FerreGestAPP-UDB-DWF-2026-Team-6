package com.ferregest.rest;

import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuración de la aplicación JAX-RS para la API REST de FerreGest.
 * Define la ruta base (/api) y registra los recursos, filtros CORS y el serializador Jackson.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Endpoints / Recursos REST (Fase 3 - Integrante 3)
        classes.add(CategoriaResource.class);
        classes.add(ProductoResource.class);
        // Filtro CORS para permitir clientes externos (React, Vue, JS puro)
        classes.add(CorsFilter.class);
        // Proveedor de serialización/deserialización JSON automática con Jackson
        classes.add(JacksonFeature.class);
        return classes;
    }
}
