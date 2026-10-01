package com.ferregest.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("stockValidator")
public class StockValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        try {
            Integer stock = Integer.parseInt(value.toString());
            if (stock < 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", "El stock no puede ser negativo.");
                throw new ValidatorException(msg);
            }
        } catch (NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de formato", "El stock ingresado no es válido.");
            throw new ValidatorException(msg);
        }
    }
}
