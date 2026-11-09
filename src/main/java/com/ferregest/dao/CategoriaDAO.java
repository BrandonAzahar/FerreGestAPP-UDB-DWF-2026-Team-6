package com.ferregest.dao;

import com.ferregest.model.Categoria;
import com.ferregest.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Categoria utilizando JPA y transacciones con Hibernate.
 * Gestiona las operaciones CRUD completas y controla el ciclo de vida del EntityManager y EntityTransaction.
 */
public class CategoriaDAO {

    /**
     * Obtiene la lista completa de categorías ordenadas por ID ascendentemente.
     *
     * @return Lista de entidades Categoria
     */
    public List<Categoria> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categoria> query = em.createQuery("SELECT c FROM Categoria c ORDER BY c.id ASC", Categoria.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar categorías con JPA: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Busca una categoría por su clave primaria (ID).
     *
     * @param id Identificador de la categoría
     * @return Entidad Categoria encontrada o null si no existe
     */
    public Categoria obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } catch (Exception e) {
            System.err.println("Error al obtener categoría por ID con JPA: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Inserta una nueva categoría en la base de datos dentro de una transacción activa.
     *
     * @param categoria Objeto Categoria a persistir
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertar(Categoria categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(categoria);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al insertar categoría en JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Actualiza los datos de una categoría existente mediante merge.
     *
     * @param categoria Objeto Categoria con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizar(Categoria categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(categoria);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al actualizar categoría en JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Elimina una categoría de la base de datos por su identificador.
     * <p>
     * CORRECCIÓN (Sprint 2): antes de borrar la categoría, los productos asociados se
     * desvinculan (categoria_id = NULL) dentro de la MISMA transacción. Así se respeta el
     * "ON DELETE SET NULL" del script SQL y no se pierden productos del inventario.
     *
     * @param id Identificador de la categoría a eliminar
     * @return true si se eliminó con éxito, false en caso de error
     */
    public boolean eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria cat = em.find(Categoria.class, id);
            if (cat != null) {
                // Paso 1: desvincular productos con una actualización masiva JPQL
                em.createQuery("UPDATE Producto p SET p.categoria = NULL WHERE p.categoria = :cat")
                  .setParameter("cat", cat)
                  .executeUpdate();
                // Paso 2: eliminar la categoría ya sin dependencias
                em.remove(cat);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al eliminar categoría en JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Verifica si ya existe otra categoría con el mismo nombre (sin distinguir mayúsculas
     * ni espacios en los extremos). Se usa en el validador JSF del formulario de categorías.
     *
     * @param nombre    Nombre a verificar
     * @param excluirId ID de la categoría que se está editando (0 si es nueva) para no compararla consigo misma
     * @return true si el nombre ya está en uso por otra categoría
     */
    public boolean existeNombre(String nombre, int excluirId) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long total = em.createQuery(
                    "SELECT COUNT(c) FROM Categoria c WHERE LOWER(TRIM(c.nombre)) = :nombre AND c.id <> :id",
                    Long.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .setParameter("id", excluirId)
                .getSingleResult();
            return total != null && total > 0;
        } catch (Exception e) {
            System.err.println("Error al verificar nombre de categoría con JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Cuenta cuántos productos pertenecen a una categoría.
     * Se usa para informar al usuario cuántos productos quedarán "Sin categoría" al eliminarla.
     *
     * @param categoriaId Identificador de la categoría
     * @return Cantidad de productos asociados (0 si ocurre un error)
     */
    public long contarProductos(int categoriaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long total = em.createQuery(
                    "SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :id", Long.class)
                .setParameter("id", categoriaId)
                .getSingleResult();
            return total != null ? total : 0L;
        } catch (Exception e) {
            System.err.println("Error al contar productos de la categoría con JPA: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
