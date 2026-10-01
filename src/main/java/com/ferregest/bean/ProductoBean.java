package com.ferregest.bean;

import com.ferregest.dao.ProductoDAO;
import com.ferregest.dao.CategoriaDAO;
import com.ferregest.model.Producto;
import com.ferregest.model.Categoria;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("productoBean")
@ViewScoped
public class ProductoBean implements Serializable {

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;
    private Producto producto;
    private List<Producto> listaProductos;
    private List<Categoria> listaCategorias;

    @PostConstruct
    public void init() {
        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();
        producto = new Producto();
        cargarDatos();
    }

    public void cargarDatos() {
        listaProductos = productoDAO.listar();
        listaCategorias = categoriaDAO.listar();
    }

    public void prepararNuevo() {
        producto = new Producto();
    }

    public void prepararEditar(Producto prod) {
        this.producto = new Producto(prod.getId(), prod.getNombre(), prod.getDescripcion(), prod.getPrecio(), prod.getStock(), prod.getCategoriaId());
    }

    public void guardar() {
        try {
            if (producto.getId() == 0) {
                productoDAO.insertar(producto);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto registrado correctamente.");
            } else {
                productoDAO.actualizar(producto);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente.");
            }
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al guardar el producto.");
        }
    }

    public void eliminar(int id) {
        try {
            productoDAO.eliminar(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado correctamente.");
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el producto.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public List<Categoria> getListaCategorias() {
        return listaCategorias;
    }

    public void setListaCategorias(List<Categoria> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }
}
