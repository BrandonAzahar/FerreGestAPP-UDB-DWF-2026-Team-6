package com.ferregest.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME =
            "ferregestPU";

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory(
                    PERSISTENCE_UNIT_NAME
            );

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void shutdown() {
        if (factory.isOpen()) {
            factory.close();
        }
    }
}
