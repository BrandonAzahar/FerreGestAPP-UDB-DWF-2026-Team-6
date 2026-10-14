package com.ferregest.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Utilidad Singleton para gestionar el ciclo de vida de EntityManagerFactory en JPA/Hibernate.
 * Proporciona instancias de EntityManager para la ejecución de operaciones y transacciones en los DAOs.
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "ferregestPU";
    private static EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("Error al inicializar EntityManagerFactory: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Retorna un nuevo EntityManager para interactuar con la base de datos.
     * Recuerde cerrarlo una vez finalizada la operación o transacción.
     *
     * @return EntityManager abierto
     */
    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return emf.createEntityManager();
    }

    /**
     * Cierra el EntityManagerFactory al apagar la aplicación.
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
