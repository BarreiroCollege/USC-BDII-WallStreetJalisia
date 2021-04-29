package gal.sdc.usc.wallstreet.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.ObjectBinding;
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

    public static class IconoObservale extends ObjectBinding<Node> {
        private final Node icono;

        public IconoObservale(FontAwesomeIcon icon) {
            this.icono = Iconos.icono(icon);
        }

        public IconoObservale(FontAwesomeIcon icon, String size) {
            this.icono = Iconos.icono(icon, size);
        }

        @Override
        public Node computeValue() {
            return icono;
        }
    }
}
