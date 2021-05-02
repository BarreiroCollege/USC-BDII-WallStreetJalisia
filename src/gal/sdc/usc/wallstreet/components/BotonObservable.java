package gal.sdc.usc.wallstreet.components;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Node;

public class BotonObservable extends ObjectBinding<Node> {
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