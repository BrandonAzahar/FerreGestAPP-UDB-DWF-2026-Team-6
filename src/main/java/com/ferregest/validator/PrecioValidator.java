package com.ferregest.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("precioValidator")
public class PrecioValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        try {
            Double precio = Double.parseDouble(value.toString());
            if (precio <= 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", "El precio debe ser mayor a 0.");
                throw new ValidatorException(msg);
            }
        } catch (NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de formato", "El precio ingresado no es válido.");
            throw new ValidatorException(msg);
        }
    }
}
