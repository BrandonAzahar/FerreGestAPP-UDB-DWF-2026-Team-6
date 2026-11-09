package com.ferregest.dao.impl;

import com.ferregest.dao.GenericDAO;
import com.ferregest.util.JPAUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import java.util.List;

public class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {

    @Override
    public T guardar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entidad);
            tx.commit();
            return entidad;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al guardar la entidad", e);
        } finally {
            em.close();
        }
    }

    @Override
    public T actualizar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entidadActualizada = em.merge(entidad);
            tx.commit();
            return entidadActualizada;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar la entidad", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(ID id, Class<T> clase) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entidad = em.find(clase, id);
            if (entidad != null) {
                em.remove(entidad);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar la entidad", e);
        } finally {
            em.close();
        }
    }

    @Override
    public T buscarPorId(Class<T> clase, ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(clase, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> listarTodos(Class<T> clase) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + clase.getSimpleName() + " e";
            return em.createQuery(jpql, clase).getResultList();
        } finally {
            em.close();
        }
    }
}