package com.ferregest.dao;

import java.util.List;

public interface GenericDAO<T, ID> {
    T guardar(T entidad);                  // Usa persist
    T actualizar(T entidad);               // Usa merge
    void eliminar(ID id, Class<T> clase);  // Usa remove
    T buscarPorId(Class<T> clase, ID id);  // Usa find
    List<T> listarTodos(Class<T> clase);
}