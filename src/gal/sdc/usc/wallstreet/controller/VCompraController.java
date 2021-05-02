package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.Main;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Comprador;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VCompraController extends DatabaseLinker {
    public static final String VIEW = "vcompra";
    public static final Integer HEIGHT = 425;
    public static final Integer WIDTH = 760;
    public static final String TITULO = "Comprar participaciones";

    @FXML
    private JFXTextField campoNumero;
    @FXML
    private JFXTextField campoSaldo;
    @FXML
    private JFXComboBox<String> empresaComboBox;
    @FXML
    private JFXTextField campoPrecio;
    @FXML
    private JFXButton btnVolver;
    @FXML
    private TableView<OfertaVenta> tablaOfertas;
    @FXML
    private TableColumn<OfertaVenta, String> nombreCol;
    @FXML
    private TableColumn<OfertaVenta, Float> precioCol;
    @FXML
    private TableColumn<OfertaVenta, Date> fechaCol;
    @FXML
    private TableColumn<OfertaVenta, Integer> cantidadCol;
    @FXML
    private JFXButton botonRefresh;

    private Usuario usr;
    private List<Empresa> listaEmpresas;
    private ObservableList<OfertaVenta> datosTabla;

    @FXML
    public void initialize() {

        listaEmpresas = new ArrayList<>();
        datosTabla = FXCollections.observableArrayList();

        // Recuperamos el usuario
        usr = super.getUsuarioSesion().getUsuario();

        // Setup de las columnas de la tabla
        tablaOfertas.setOnSort(Event::consume); // Impedimos reordenamiento
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<>("restantes"));
        tablaOfertas.setItems(datosTabla);
        tablaOfertas.setSelectionModel(null); // Evitamos que se seleccionen filas (estético)
        tablaOfertas.setPlaceholder(new Label("")); // Eliminamos el texto por defecto (estético)
        nombreCol.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(p.getValue().getUsuario().getIdentificador());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        // Formateo de los campos numericos
        campoNumero.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) campoNumero.setText(oldValue);
        });
        campoPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*|\\d+\\.\\d{0,2}")) campoPrecio.setText(oldValue);
        });

        // Cuando se cambie el precio se actualizan las ofertas en base al nuevo
        campoPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarDatosTabla();
        });

        // Boton de refresh
        botonRefresh.setGraphic(Iconos.icono(FontAwesomeIcon.REFRESH, "1em"));

        actualizarVentana();
    }

    // FUNCIONALIDADES //

    public void actualizarSaldo() {
        usr = super.getDAO(UsuarioDAO.class).seleccionar(usr.getSuperUsuario());
        campoSaldo.setText(String.valueOf(usr.getSaldo() - usr.getSaldoBloqueado()));
    }

    public void actualizarListaEmpresas() {
        // Se carga la nueva lista
        listaEmpresas = super.getDAO(EmpresaDAO.class).getEmpresas();
        // Se limpia la comboBox y se vuelve a llenar
        empresaComboBox.getItems().clear();
        for (Empresa e : listaEmpresas) {
            empresaComboBox.getItems().add(e.getNombre() + " || " + e.getCif());
        }
    }

    public void actualizarDatosTabla() {
        // Si no hay ninguna empresa seleccionada, se limpia la tabla
        if (empresaComboBox.getSelectionModel().getSelectedIndex() == -1) {
            datosTabla.clear();
            return;
        }
        String identificador = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getUsuario().getSuperUsuario().getIdentificador();
        datosTabla.setAll(getDAO(OfertaVentaDAO.class).getOfertasVenta(identificador, campoPrecio.getText().isEmpty() ? 0f : Float.parseFloat(campoPrecio.getText())));
    }

    public void actualizarVentana() {
        actualizarSaldo();
        actualizarListaEmpresas();
        actualizarDatosTabla();
        campoNumero.setText("");
        campoPrecio.setText("");
    }

    public void comprar() {
        // Si alguno de los campos necesarios está vacío o es 0 no se hace nada
        if (campoPrecio.getText().isEmpty() || Float.parseFloat(campoPrecio.getText()) == 0 || campoNumero.getText().isEmpty() || Integer.parseInt(campoNumero.getText()) == 0 || empresaComboBox.getSelectionModel().getSelectedIndex() == -1 || Float.parseFloat(campoSaldo.getText()) == 0) {
            Main.mensaje("Introduzca valores válidos", 3);
            return;
        }

        super.iniciarTransaccion();

        actualizarDatosTabla();
        actualizarSaldo();

        // Variables de estado
        float saldoInicial = Float.parseFloat(campoSaldo.getText());

        Integer compradas = Comprador.comprar(usr, datosTabla, Integer.parseInt(campoNumero.getText()));

        String mensaje;
        // Tratamos de comprometer la transacción e informamos al usuario
        if (super.ejecutarTransaccion()) {
            if (compradas == 0) mensaje = "No dispone de suficiente saldo";
            else
                mensaje = "Éxito. Se compraron " + compradas + " participaciones a una media de "
                        + new DecimalFormat("0.00").format((saldoInicial - usr.getSaldoDisponible()) / compradas)
                        + " €/participacion";
        } else {
            mensaje = "Compra fallida!";
        }

        // Actualizamos los elementos gráficos
        Main.mensaje(mensaje, 3);
        actualizarVentana();
    }

    // BOTONES //

    // Boton de salir
    public void btnVolverEvent(ActionEvent event) {
        Main.ventana(
                PrincipalController.VIEW,
                PrincipalController.WIDTH,
                PrincipalController.HEIGHT,
                PrincipalController.TITULO
        );
    }

    // Nueva empresa seleccionada
    public void btnEmpresaEvent(ActionEvent event) {
        actualizarDatosTabla();
    }

    // Boton de comprar
    public void btnComprarEvent(ActionEvent event) {
        comprar();
    }

    // Boton de refresco
    public void btnRefreshEvent(ActionEvent event) {
        actualizarVentana();
    }


}
