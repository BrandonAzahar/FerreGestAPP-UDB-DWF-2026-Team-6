package com.ferregest.dto;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) para la entidad Categoria.
 * Desacopla la capa de persistencia (JPA) del transporte en formato JSON hacia la API REST.
 */
public class CategoriaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String descripcion;
    private Long totalProductos;

    public CategoriaDTO() {
    }

    public CategoriaDTO(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public CategoriaDTO(int id, String nombre, String descripcion, Long totalProductos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.totalProductos = totalProductos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }
}
