package gal.sdc.usc.wallstreet.util;

import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public class Validadores {
    public static RequiredFieldValidator requerido() {
        RequiredFieldValidator validator = new RequiredFieldValidator("Completa este campo");
        validator.setIcon(Iconos.icono(FontAwesomeIcon.WARNING, "1em"));
        validator.getIcon().setStyle("-fx-font-family: FontAwesome;");
        return validator;
    }

    public static ErrorValidator personalizado(String msg) {
        ErrorValidator error = new ErrorValidator(msg);
        error.setIcon(Iconos.icono(FontAwesomeIcon.WARNING));
        error.getIcon().setStyle("-fx-font-family: FontAwesome;");
        return error;
    }
}
