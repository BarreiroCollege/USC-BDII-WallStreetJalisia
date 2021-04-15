package gal.sdc.usc.wallstreet.util;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;

@DefaultProperty("icon")
public class ErrorValidator extends ValidatorBase {
    public ErrorValidator(String message) {
        super(message);
        this.hasErrors.set(true);
    }

    public ErrorValidator() {
        this.hasErrors.set(true);
    }

    protected void eval() {
    }
}