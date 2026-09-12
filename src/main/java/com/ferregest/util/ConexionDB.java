package com.ferregest.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase encargada de gestionar la conexión a la base de datos MySQL.
 * Utiliza el patrón de diseño Singleton para asegurar que solo exista
 * una instancia de la conexión en toda la aplicación.
 */
public class ConexionDB {
    // Instancia única de la clase (Singleton)
    private static ConexionDB instance;
    
    // Credenciales de acceso a la BD
    private String url;
    private String username;
    private String password;

    /**
     * Constructor privado para evitar que se instancie desde fuera de la clase.
     */
    private ConexionDB() {
        // OPTIMIZACIÓN: Se usa try-with-resources para cerrar automáticamente el InputStream
        // evitando fugas de memoria (memory leaks).
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (in != null) {
                props.load(in);
                this.url = props.getProperty("db.url");
                this.username = props.getProperty("db.user");
                this.password = props.getProperty("db.password");
                String driver = props.getProperty("db.driver");
                
                // Registramos el driver de MySQL
                Class.forName(driver);
            } else {
                throw new RuntimeException("No se encontró el archivo db.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar la configuración de base de datos", e);
        }
    }

    /**
     * Método para obtener la única instancia de la clase de configuración.
     */
    public static synchronized ConexionDB getInstance() {
        if (instance == null) {
            instance = new ConexionDB();
        }
        return instance;
    }

    /**
     * CORRECCIÓN DE CONCURRENCIA: 
     * Retorna una NUEVA conexión cada vez que un DAO la solicita.
     * Esto evita que múltiples usuarios (hilos) choquen al usar la misma conexión.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
