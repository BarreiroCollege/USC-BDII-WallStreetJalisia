package gal.sdc.usc.wallstreet.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;

public class Iconos {
    public static Node icono(FontAwesomeIcon icon) {
        return icono(icon, "1.5em");
    }

    public static Node icono(FontAwesomeIcon icon, String size) {
        FontAwesomeIconView view = new FontAwesomeIconView(icon);
        view.setSize(size);
        view.setStyleClass("icon");
        return view;
    }
}
