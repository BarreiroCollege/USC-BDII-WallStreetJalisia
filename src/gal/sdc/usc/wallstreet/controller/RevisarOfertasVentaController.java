package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextField;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.SuperUsuario;
import gal.sdc.usc.wallstreet.model.ddl.Entidad;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.InversorDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.ReguladorDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comunicador;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RevisarOfertasVentaController extends DatabaseLinker {

    private static JFXSnackbar snackbar;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXButton btnVerUsuario;
    @FXML
    private Label txtUsuarioSociedad;
    @FXML
    private JFXTextField txtComisionPorDefecto;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnRechazar;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnAnterior;
    @FXML
    private Label txtFecha;
    @FXML
    private Label txtEmpresa;
    @FXML
    private Label txtUsuario;
    @FXML
    private Label txtNumPart;
    @FXML
    private Label txtPrecioVenta;
    private List<OfertaVenta> ofertasPendientes;
    private OfertaVenta ofertaActual;            // Oferta que el regulador está viendo
    private String ordenElegido;

    public static void mensaje(String mensaje) {
        mensaje(mensaje, null);
    }

    public static void mensaje(String mensaje, Integer duracion) {
        mensaje(new JFXSnackbarLayout(mensaje), duracion);
    }

    private static void mensaje(JFXSnackbarLayout layout, Integer duracion) {
        JFXSnackbarLayout finalLayout = new JFXSnackbarLayout(layout.getToast(), "Cerrar", e -> snackbar.close());
        if (duracion != null) {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout, Duration.seconds(duracion)));
        } else {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(finalLayout));
        }
    }

    public void initialize() {
        obtenerDatos();                 // Se buscan las ofertas pendientes

        // Si hay más de una, se da opción a ordenarlas.
        if (ofertasPendientes.size() > 1) abrirOrdenar();

        reordenar();
        ofertaActual = ofertasPendientes.get(0);
        mostrarDatos();             // Se muestran los datos de la oferta actual
        controlarVisibilidadesAnteriorPosterior();

        // Se registra la snackbar
        snackbar = new JFXSnackbar(anchorPane);
    }

    public void obtenerDatos() {
        ofertasPendientes = super.getDAO(OfertaVentaDAO.class).getOfertasPendientes();
    }

    public void mostrarDatos() {
        txtFecha.setText(ofertaActual.getFecha().toString());
        txtEmpresa.setText(ofertaActual.getEmpresa().getNombre());
        txtUsuario.setText(ofertaActual.getUsuario().getIdentificador());
        txtNumPart.setText(ofertaActual.getNumParticipaciones().toString());
        txtPrecioVenta.setText(ofertaActual.getPrecioVenta().toString());
        // La comisión por defecto es 0.05 para usuarios y 0.04 para sociedades
        txtComisionPorDefecto.setText(ofertaActual.getComision().toString());

        if (Math.abs(
                ofertaActual.getComision() - super.getDAO(ReguladorDAO.class).getRegulador().getComision()
        ) < 0.005) {       // La comisión es un punto flotante
            txtUsuarioSociedad.setText("Usuario");
            btnVerUsuario.setVisible(true);
        } else {        // Comision no corresponde a un usuario -> La oferta de venta fue creada por una sociedad
            txtUsuarioSociedad.setText("Sociedad");
            // Las sociedades no tienen atributos de interés más allá del saldo comunal y la tolerancia
            btnVerUsuario.setVisible(false);
        }
    }

    // Botón de retroceso
    public void anterior() {
        ofertaActual = ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) - 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    // Botón de avance
    public void siguiente() {
        ofertaActual = ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) + 1);
        mostrarDatos();
        controlarVisibilidadesAnteriorPosterior();
    }

    public void controlarVisibilidadesAnteriorPosterior() {
        btnSiguiente.setVisible(ofertasPendientes.indexOf(ofertaActual) != ofertasPendientes.size() - 1);
        btnAnterior.setVisible(ofertasPendientes.indexOf(ofertaActual) != 0);
    }

    /**
     * Rechaza la oferta que se está mostrando.
     * <p>
     * Se admite un nivel de aislamiento con lecturas no comprometidas, puesto que es imposible que se produzcan cambios
     * sobre la oferta de venta si el regulador aún no la ha aceptado/rechazado. Esto acelera la ejecución concurrente.
     */
    public void rechazar() {
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(OfertaVentaDAO.class).rechazarOfertaVenta(ofertaActual);
        if (super.ejecutarTransaccion()) mensaje("Oferta rechazada correctamente", 3);
        else {
            mensaje("Error al rechazar la oferta", 3);
            return;
        }

        cambiarOferta();
    }

    /**
     * Se acepta la oferta de venta actual.
     * <p>
     * Se admite un nivel de aislamiento con lecturas no comprometidas, puesto que es imposible que se produzcan cambios
     * sobre la oferta de venta si el regulador aún no la ha aceptado/rechazado. Esto acelera la ejecución concurrente.
     */
    public void aceptar() {
        super.iniciarTransaccion(Connection.TRANSACTION_READ_UNCOMMITTED);
        super.getDAO(OfertaVentaDAO.class).aceptarOfertaVenta(ofertaActual);
        if (super.ejecutarTransaccion()) mensaje("Oferta aceptada correctamente", 3);
        else {
            mensaje("Error al aceptar la oferta", 3);
            return;
        }

        cambiarOferta();
    }

    /***
     * Cambia de oferta. La actual se elimina y se pasa a la siguiente, si la hay. Si no, se retrocede a la anterior.
     * Si esta era la última oferta, se cierra la ventana.
     */
    public void cambiarOferta() {
        // Se retira la oferta de la lista de pendientes y se determina qué botones mostrar
        if (ofertasPendientes.indexOf(ofertaActual) != ofertasPendientes.size() - 1) {
            siguiente();
            ofertasPendientes.remove(ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) - 1));
            controlarVisibilidadesAnteriorPosterior();
        } else if (ofertasPendientes.indexOf(ofertaActual) != 0) {
            anterior();
            ofertasPendientes.remove(ofertasPendientes.get(ofertasPendientes.indexOf(ofertaActual) + 1));
            controlarVisibilidadesAnteriorPosterior();
        } else {
            Main.aviso("No quedan ofertas de venta por revisar");
            cerrarVentana();
        }
    }

    public void cerrarVentana() {
        Stage stage = (Stage) btnSiguiente.getScene().getWindow();
        stage.close();
    }

    public void abrirOrdenar() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Orden de creación");
        opciones.add("Número de participaciones");
        opciones.add("Precio de venta");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Orden");
        dialog.setHeaderText("¿En qué orden se deben mostrar las ofertas de venta?");
        dialog.setContentText("Elegir opción:");

        Optional<String> input = dialog.showAndWait();
        input.ifPresent(opcion -> ordenElegido = opcion);
        dialog.close();
    }

    public void reordenar() {
        if (ordenElegido == null || ordenElegido.equals("Orden de creación")) {
            // Orden en el que llegaron las ofertas a la aplicación
            ofertasPendientes.sort(Comparator.comparing(OfertaVenta::getFecha));
        } else if (ordenElegido.equals("Número de participaciones")) {
            ofertasPendientes.sort(Comparator.comparing(OfertaVenta::getNumParticipaciones));
        } else {        // Se ordenan por precio de venta
            ofertasPendientes.sort(Comparator.comparing(OfertaVenta::getPrecioVenta));
        }
    }

    /**
     * Se abre una nueva ventana que muestra los datos de la empresa referenciada por la oferta actual.
     */
    public void mostrarEmpresa() {
        Comunicador comunicador = new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[]{ofertaActual.getEmpresa()};
            }
        };
        VerUsuarioController.setComunicador(comunicador);

        Parent root = Main.getView("verusuario");
        Stage stage = new Stage();
        stage.setTitle("Empresa de la oferta de venta");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 350, 500));
        // La ventana de ofertas de venta es la ventana padre. Queda visible, pero desactivada.
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnAceptar.getScene().getWindow());
        stage.show();
    }

    /**
     * Se abre una nueva ventana que muestra los datos del usuario que creó la oferta de venta actual.
     */
    public void mostrarUsuario() {
        SuperUsuario superUsuarioOferta = ofertaActual.getUsuario();
        Entidad usuario = super.getDAO(InversorDAO.class).getInversor(superUsuarioOferta.getIdentificador());
        if (usuario == null) {
            usuario = super.getDAO(EmpresaDAO.class).getEmpresa(superUsuarioOferta.getIdentificador());
        }

        // Entidad final (necesaria para la interfaz)
        Entidad finalUsuario = usuario;
        Comunicador comunicador = new Comunicador() {
            @Override
            public Object[] getData() {
                return new Object[]{finalUsuario};
            }
        };
        VerUsuarioController.setComunicador(comunicador);

        Parent root = Main.getView("verusuario");
        Stage stage = new Stage();
        stage.setTitle("Usuario creador de la oferta de venta");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 350, 500));
        // La ventana de ofertas de venta es la ventana padre. Queda visible, pero desactivada.
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnAceptar.getScene().getWindow());
        stage.show();
    }
}
