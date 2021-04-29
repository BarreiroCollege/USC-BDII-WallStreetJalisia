package gal.sdc.usc.wallstreet.components;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Node;

public class BotonObservable extends ObjectBinding<JFXButton> {
    private final JFXButton boton;

    public BotonObservable(JFXButton boton) {
        this.boton = boton;
    }

    public JFXButton getBoton() {
        return boton;
    }

    @Override
    public JFXButton computeValue() {
        return boton;
    }
}