package gal.sdc.usc.wallstreet.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Node;

public class IconoObservable extends ObjectBinding<Node> {
    private final Node icono;

    public IconoObservable(FontAwesomeIcon icon) {
        this.icono = Iconos.icono(icon);
    }

    public IconoObservable(FontAwesomeIcon icon, String size) {
        this.icono = Iconos.icono(icon, size);
    }

    @Override
    public Node computeValue() {
        return icono;
    }
}