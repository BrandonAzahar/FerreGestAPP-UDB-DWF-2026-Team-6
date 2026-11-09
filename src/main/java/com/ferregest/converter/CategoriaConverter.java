package com.ferregest.converter;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.model.Categoria;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("categoriaConverter")
public class CategoriaConverter implements Converter<Object> {

    private CategoriaDAO categoriaDAO;

    public CategoriaConverter() {
        this.categoriaDAO = new CategoriaDAO();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                int id = Integer.parseInt(value);
                return categoriaDAO.obtenerPorId(id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && value instanceof Categoria) {
            return String.valueOf(((Categoria) value).getId());
        }
        return "";
    }
}
