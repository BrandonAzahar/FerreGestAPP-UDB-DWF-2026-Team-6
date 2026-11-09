package com.ferregest.bean;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.dao.ProductoDAO;
import com.ferregest.model.Categoria;
import com.ferregest.model.Producto;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Managed Bean de JSF y CDI encargado de la gestión de Productos.
 * Conectado con ProductoDAO y CategoriaDAO vía JPA/Hibernate.
 * Alcance @ViewScoped para sincronización reactiva por AJAX.
 */
@Named("productoBean")
@ViewScoped
public class ProductoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;

    private Producto producto;
    private Integer categoriaSeleccionadaId;

    private List<Producto> listaProductos;
    private List<Categoria> listaCategorias;

    /**
     * Inicialización del ciclo de vida del bean.
     */
    @PostConstruct
    public void init() {
        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();
        prepararNuevo();
        cargarDatos();
    }

    /**
     * Recarga las listas de productos y categorías desde la base de datos vía JPA.
     */
    public void cargarDatos() {
        listaProductos = productoDAO.listar();
        listaCategorias = categoriaDAO.listar();
    }

    /**
     * Inicializa un nuevo producto y limpia la selección del formulario.
     */
    public void prepararNuevo() {
        this.producto = new Producto();
        this.categoriaSeleccionadaId = null;
    }

    /**
     * Carga un producto existente en el formulario para su edición.
     *
     * @param prod Producto seleccionado de la tabla
     */
    public void prepararEditar(Producto prod) {
        this.producto = new Producto(
            prod.getId(), 
            prod.getNombre(), 
            prod.getDescripcion(), 
            prod.getPrecio(), 
            prod.getStock(), 
            prod.getCategoria()
        );
        this.categoriaSeleccionadaId = (prod.getCategoria() != null) ? prod.getCategoria().getId() : null;
    }

    /**
     * Guarda un producto nuevo o actualiza uno existente usando JPA y transacciones reales.
     */
    public void guardar() {
        try {
            // CORRECCIÓN (Sprint 2): la vista original enlazaba el combo a "producto.categoriaId"
            // pero aquí solo se revisaba "categoriaSeleccionadaId", por lo que crear productos
            // siempre fallaba con "Categoría requerida". Ahora la fuente única es
            // producto.getCategoriaId(); setCategoriaSeleccionadaId() la mantiene sincronizada,
            // así funcionan tanto la nueva vista productos.xhtml como las vistas originales.
            Integer categoriaId = producto.getCategoriaId();

            // Validación de categoría seleccionada
            if (categoriaId == null || categoriaId <= 0) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Categoría requerida", "Debe seleccionar una categoría para el producto.");
                return;
            }

            Categoria categoriaSeleccionada = categoriaDAO.obtenerPorId(categoriaId);
            if (categoriaSeleccionada == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", "La categoría seleccionada no existe.");
                return;
            }

            // Normalizar espacios del texto antes de persistir
            if (producto.getNombre() != null) {
                producto.setNombre(producto.getNombre().trim());
            }
            if (producto.getDescripcion() != null) {
                producto.setDescripcion(producto.getDescripcion().trim());
            }

            // Asociar la entidad Categoria al producto
            producto.setCategoria(categoriaSeleccionada);

            boolean exito;
            if (producto.getId() == 0) {
                exito = productoDAO.insertar(producto);
                if (exito) {
                    addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto '" + producto.getNombre() + "' registrado correctamente.");
                } else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el producto en la base de datos.");
                    return;
                }
            } else {
                exito = productoDAO.actualizar(producto);
                if (exito) {
                    addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto '" + producto.getNombre() + "' actualizado correctamente.");
                } else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el producto.");
                    return;
                }
            }

            prepararNuevo();
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error inesperado al guardar el producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina un producto por su identificador único.
     *
     * @param id ID del producto
     */
    public void eliminar(int id) {
        try {
            boolean exito = productoDAO.eliminar(id);
            if (exito) {
                addMessage(FacesMessage.SEVERITY_INFO, "Eliminación exitosa", "El producto ha sido eliminado del inventario.");
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el producto de la base de datos.");
            }
            prepararNuevo();
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al intentar eliminar el producto.");
            e.printStackTrace();
        }
    }

    /**
     * Encola un mensaje para ser mostrado en el componente <h:messages>.
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters y Setters
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCategoriaSeleccionadaId() {
        return categoriaSeleccionadaId;
    }

    /**
     * Recibe el ID elegido en el combo de categorías de productos.xhtml y lo sincroniza
     * con el producto en edición, que es la fuente única usada por guardar().
     */
    public void setCategoriaSeleccionadaId(Integer categoriaSeleccionadaId) {
        this.categoriaSeleccionadaId = categoriaSeleccionadaId;
        if (producto != null) {
            producto.setCategoriaId(categoriaSeleccionadaId);
        }
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
