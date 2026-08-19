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
    private Connection connection;
    
    // Credenciales de acceso a la BD
    private String url;
    private String username;
    private String password;

    /**
     * Constructor privado para evitar que se instancie desde fuera de la clase.
     * Aquí se lee el archivo de propiedades para obtener las credenciales.
     */
    private ConexionDB() {
        try {
            Properties props = new Properties();
            // Cargamos el archivo db.properties desde la ruta src/main/resources
            InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties");
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
     * Método para obtener la única instancia de la clase.
     * Es synchronized para prevenir problemas en entornos multihilo.
     */
    public static synchronized ConexionDB getInstance() {
        if (instance == null) {
            instance = new ConexionDB();
        }
        return instance;
    }

    /**
     * Retorna la conexión activa a la base de datos.
     * Si está cerrada o es nula, crea una nueva.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }
}
