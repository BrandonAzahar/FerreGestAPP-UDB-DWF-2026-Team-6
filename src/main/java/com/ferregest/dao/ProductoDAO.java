package com.ferregest.dao;

import com.ferregest.model.Categoria;
import com.ferregest.model.Producto;
import com.ferregest.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Producto utilizando JPA y transacciones con Hibernate.
 * Gestiona el ciclo de vida de persistencia, relaciones con Categoria y transacciones reales.
 */
public class ProductoDAO {

    /**
     * Lista todos los productos registrados cargando su categoría relacionada mediante LEFT JOIN FETCH.
     *
     * @return Lista de entidades Producto
     */
    public List<Producto> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria ORDER BY p.id ASC", 
                Producto.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar productos con JPA: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Busca un producto por su clave primaria (ID).
     *
     * @param id Identificador único del producto
     * @return Entidad Producto o null si no se encuentra
     */
    public Producto obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Producto.class, id);
        } catch (Exception e) {
            System.err.println("Error al obtener producto por ID con JPA: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Inserta un nuevo producto dentro de una transacción activa en la base de datos.
     *
     * @param producto Objeto Producto a persistir
     * @return true si la operación tuvo éxito, false en caso de error
     */
    public boolean insertar(Producto producto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Si tiene una categoría asociada, asegurar que esté administrada por el contexto de persistencia
            if (producto.getCategoria() != null && producto.getCategoria().getId() > 0) {
                Categoria categoriaGestionada = em.find(Categoria.class, producto.getCategoria().getId());
                producto.setCategoria(categoriaGestionada);
            }
            em.persist(producto);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al insertar producto con JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Actualiza la información de un producto existente utilizando merge.
     *
     * @param producto Objeto Producto con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso de error
     */
    public boolean actualizar(Producto producto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Asociar la categoría correspondiente gestionada por JPA
            if (producto.getCategoria() != null && producto.getCategoria().getId() > 0) {
                Categoria categoriaGestionada = em.find(Categoria.class, producto.getCategoria().getId());
                producto.setCategoria(categoriaGestionada);
            } else {
                producto.setCategoria(null);
            }
            em.merge(producto);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al actualizar producto con JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Lista todos los productos que pertenecen a una categoría específica.
     * Carga de forma anticipada (LEFT JOIN FETCH) la relación con Categoria.
     *
     * @param categoriaId Identificador único de la categoría
     * @return Lista de entidades Producto asociadas a dicha categoría
     */
    public List<Producto> listarPorCategoria(int categoriaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE p.categoria.id = :categoriaId ORDER BY p.id ASC",
                Producto.class
            );
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar productos por categoría con JPA: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Busca productos cuyo nombre coincida de forma parcial con el término proporcionado.
     * La búsqueda es insensible a mayúsculas/minúsculas.
     *
     * @param termino Texto a buscar en el nombre del producto
     * @return Lista de entidades Producto que coinciden con el criterio
     */
    public List<Producto> buscarPorNombre(String termino) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE LOWER(p.nombre) LIKE LOWER(:termino) ORDER BY p.id ASC",
                Producto.class
            );
            query.setParameter("termino", "%" + (termino != null ? termino.trim() : "") + "%");
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al buscar productos por nombre con JPA: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Elimina un producto de la base de datos según su identificador.
     *
     * @param id Clave primaria del producto a eliminar
     * @return true si fue eliminado correctamente, false si ocurrió un error
     */
    public boolean eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Producto prod = em.find(Producto.class, id);
            if (prod != null) {
                em.remove(prod);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al eliminar producto con JPA: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
