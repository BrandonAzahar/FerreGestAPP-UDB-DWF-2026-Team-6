package com.ferregest.model;

/**
 * Clase Modelo (POJO) que representa la tabla 'categorias' de la base de datos.
 * Aplica el principio de encapsulamiento con atributos privados y getters/setters públicos.
 */
public class Categoria {
    private int id;
    private String nombre;
    private String descripcion;

    // Constructor vacío (obligatorio para convenciones de JavaBeans)
    public Categoria() {
    }

    // Constructor parametrizado para facilitar la creación de objetos
    public Categoria(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
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

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
