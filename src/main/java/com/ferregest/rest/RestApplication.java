package com.ferregest.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // Esta clase le dice a Tomcat que la ruta base de la API será /api
}