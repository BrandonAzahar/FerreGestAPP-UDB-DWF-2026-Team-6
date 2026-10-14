package com.ferregest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidad JPA que representa la tabla 'productos' en la base de datos MySQL.
 * Contiene los atributos del producto y la relación Muchos a Uno (@ManyToOne) con Categoria.
 */
@Entity
@Table(name = "productos")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private double precio;

    @Column(name = "stock", nullable = false)
    private int stock;

    // Relación Muchos a Uno: Varios productos pertenecen a una misma categoría
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = true)
    private Categoria categoria;

    // Constructor vacío requerido por JPA
    public Producto() {
    }

    // Constructor sin ID para nuevos productos
    public Producto(String nombre, String descripcion, double precio, int stock, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // Constructor completo con entidad Categoria
    public Producto(int id, String nombre, String descripcion, double precio, int stock, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // Constructor de conveniencia compatible con identificador numérico de categoría
    public Producto(int id, String nombre, String descripcion, double precio, int stock, int categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        if (categoriaId > 0) {
            this.categoria = new Categoria();
            this.categoria.setId(categoriaId);
        }
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Método auxiliar de conveniencia para obtener el ID de la categoría asociada
     * de forma segura para vistas y componentes JSF.
     */
    public Integer getCategoriaId() {
        return (categoria != null) ? categoria.getId() : null;
    }

    /**
     * Asigna una categoría básica a través de su ID numérico.
     */
    public void setCategoriaId(Integer catId) {
        if (catId != null && catId > 0) {
            // CORRECCIÓN (Sprint 2): antes se modificaba el ID del objeto Categoria existente,
            // el cual puede estar compartido con otros productos de la lista (efecto colateral).
            // Ahora se crea una referencia nueva; el DAO la reemplaza por la entidad gestionada.
            if (this.categoria == null || this.categoria.getId() != catId) {
                this.categoria = new Categoria();
                this.categoria.setId(catId);
            }
        } else {
            this.categoria = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return id == producto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "Sin Categoría") +
                '}';
    }
}
