package com.ferregest.config;

import com.ferregest.rest.CategoriaResource;
import com.ferregest.rest.CorsFilter;
import com.ferregest.rest.ProductoResource;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuración central de JAX-RS (Jersey) para la API REST de FerreGest (Fase 3).
 * Extiende de {@link Application} y define la ruta base "/api" mediante {@link ApplicationPath}.
 */
@ApplicationPath("/api")
public class RestConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Recursos REST de Categorías y Productos
        classes.add(CategoriaResource.class);
        classes.add(ProductoResource.class);
        // Filtro CORS para permitir el consumo desde clientes externos con Fetch API
        classes.add(CorsFilter.class);
        // Integración con Jackson para serialización y deserialización JSON
        classes.add(JacksonFeature.class);
        return classes;
    }
}
