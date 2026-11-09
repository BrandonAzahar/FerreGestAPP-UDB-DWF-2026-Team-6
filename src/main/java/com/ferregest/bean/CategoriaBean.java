package com.ferregest.bean;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.model.Categoria;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Managed Bean de JSF y CDI encargado de la gestión de Categorías.
 * Alcance @ViewScoped para mantener el estado del formulario y la tabla de forma reactiva con AJAX.
 * <p>
 * Separación de responsabilidades (Sprint 2):
 * <ul>
 *     <li>La vista (categorias.xhtml) solo muestra datos y dispara acciones.</li>
 *     <li>Este bean coordina la presentación: mensajes, estado del formulario y validaciones.</li>
 *     <li>CategoriaDAO encapsula el acceso a datos con JPA/Hibernate y transacciones.</li>
 * </ul>
 */
@Named("categoriaBean")
@ViewScoped
public class CategoriaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Longitud mínima aceptada para el nombre de una categoría. */
    private static final int NOMBRE_MIN = 3;

    /** Longitud máxima del nombre (coincide con @Column(length = 100) de la entidad). */
    private static final int NOMBRE_MAX = 100;

    private CategoriaDAO categoriaDAO;
    private Categoria categoria;
    private List<Categoria> listaCategorias;

    /**
     * Inicialización del Managed Bean tras su creación.
     */
    @PostConstruct
    public void init() {
        categoriaDAO = new CategoriaDAO();
        categoria = new Categoria();
        cargarCategorias();
    }

    /**
     * Recarga el listado de categorías desde la base de datos a través de JPA.
     */
    public void cargarCategorias() {
        listaCategorias = categoriaDAO.listar();
    }

    /**
     * Limpia el formulario para registrar una nueva categoría.
     */
    public void prepararNuevo() {
        this.categoria = new Categoria();
    }

    /**
     * Carga los datos de una categoría seleccionada en el formulario para su edición.
     * Se crea una copia para que los cambios no afecten la fila de la tabla hasta guardar.
     *
     * @param cat Categoría a editar
     */
    public void prepararEditar(Categoria cat) {
        this.categoria = new Categoria(cat.getId(), cat.getNombre(), cat.getDescripcion());
    }

    /**
     * Guarda o actualiza una categoría en la base de datos usando JPA.
     */
    public void guardar() {
        try {
            // Normalizar espacios antes de persistir
            if (categoria.getNombre() != null) {
                categoria.setNombre(categoria.getNombre().trim());
            }
            if (categoria.getDescripcion() != null) {
                categoria.setDescripcion(categoria.getDescripcion().trim());
            }

            boolean exito;
            if (categoria.getId() == 0) {
                exito = categoriaDAO.insertar(categoria);
                if (exito) {
                    addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Categoría '" + categoria.getNombre() + "' registrada correctamente.");
                } else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la categoría.");
                    return;
                }
            } else {
                exito = categoriaDAO.actualizar(categoria);
                if (exito) {
                    addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Categoría '" + categoria.getNombre() + "' actualizada correctamente.");
                } else {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la categoría.");
                    return;
                }
            }
            prepararNuevo();
            cargarCategorias();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error inesperado", "Ocurrió un error al procesar la categoría: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina una categoría por su ID.
     * Los productos asociados NO se borran: quedan "Sin categoría" (ver CategoriaDAO.eliminar).
     *
     * @param id ID de la categoría a eliminar
     */
    public void eliminar(int id) {
        try {
            // Se cuentan antes de borrar para informar al usuario del efecto de la operación
            long productosAfectados = categoriaDAO.contarProductos(id);

            boolean exito = categoriaDAO.eliminar(id);
            if (exito) {
                String detalle = "La categoría ha sido eliminada.";
                if (productosAfectados > 0) {
                    detalle += " " + productosAfectados + " producto(s) quedaron sin categoría asignada.";
                }
                addMessage(FacesMessage.SEVERITY_INFO, "Eliminación exitosa", detalle);
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la categoría. Verifique la conexión a la base de datos.");
            }
            prepararNuevo();
            cargarCategorias();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al intentar eliminar la categoría.");
            e.printStackTrace();
        }
    }

    /**
     * Validador JSF personalizado para el campo "nombre" (se invoca desde la vista con
     * validator="#{categoriaBean.validarNombre}").
     * Reglas:
     * <ol>
     *     <li>No puede contener solo espacios y debe tener al menos 3 caracteres.</li>
     *     <li>No puede repetirse con otra categoría existente (ignora mayúsculas).</li>
     * </ol>
     * Si falla, lanza ValidatorException y JSF muestra el mensaje en el h:message del campo,
     * sin ejecutar el método guardar().
     */
    public void validarNombre(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return; // El atributo required="true" de la vista se encarga del caso vacío
        }

        String nombre = value.toString().trim();

        if (nombre.length() < NOMBRE_MIN) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Nombre inválido", "El nombre debe tener al menos " + NOMBRE_MIN + " caracteres (sin contar espacios)."));
        }

        if (nombre.length() > NOMBRE_MAX) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Nombre inválido", "El nombre no puede superar " + NOMBRE_MAX + " caracteres."));
        }

        int idActual = (categoria != null) ? categoria.getId() : 0;
        if (categoriaDAO.existeNombre(nombre, idActual)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Nombre duplicado", "Ya existe una categoría llamada '" + nombre + "'."));
        }
    }

    /**
     * Encola un mensaje para ser renderizado por <h:messages> en la vista JSF.
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters y Setters
    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Categoria> getListaCategorias() {
        return listaCategorias;
    }

    public void setListaCategorias(List<Categoria> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }
}
