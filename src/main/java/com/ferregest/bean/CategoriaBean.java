package com.ferregest.bean;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.model.Categoria;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("categoriaBean")
@ViewScoped
public class CategoriaBean implements Serializable {

    private CategoriaDAO categoriaDAO;
    private Categoria categoria;
    private List<Categoria> listaCategorias;

    @PostConstruct
    public void init() {
        categoriaDAO = new CategoriaDAO();
        categoria = new Categoria();
        cargarCategorias();
    }

    public void cargarCategorias() {
        listaCategorias = categoriaDAO.listar();
    }

    public void prepararNuevo() {
        categoria = new Categoria();
    }

    public void prepararEditar(Categoria cat) {
        this.categoria = new Categoria(cat.getId(), cat.getNombre(), cat.getDescripcion());
    }

    public void guardar() {
        try {
            if (categoria.getId() == 0) {
                categoriaDAO.insertar(categoria);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Categoría creada correctamente.");
            } else {
                categoriaDAO.actualizar(categoria);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Categoría actualizada correctamente.");
            }
            cargarCategorias();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al guardar la categoría.");
        }
    }

    public void eliminar(int id) {
        try {
            categoriaDAO.eliminar(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Categoría eliminada correctamente.");
            cargarCategorias();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la categoría.");
        }
    }

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
