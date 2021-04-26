package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.VentaDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.Iconos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class VVentaController extends DatabaseLinker {

    public static final String VIEW = "VVenta";
    public static final Integer HEIGHT = 425;
    public static final Integer WIDTH = 760;
    public static final String TITULO = "Venta";

    private Usuario usr;
    private ObservableList<OfertaVenta> datosTabla;

    @FXML
    private JFXButton btnVolver;
    @FXML
    private TableView<OfertaVenta> tablaOfertas;
    @FXML
    private TableColumn<OfertaVenta,String> empresaCol;
    @FXML
    private TableColumn<OfertaVenta,Integer> ofertadasCol;
    @FXML
    private TableColumn<OfertaVenta,Integer> restantesCol;
    @FXML
    private TableColumn<OfertaVenta,Float> precioCol;
    @FXML
    private TableColumn<OfertaVenta,Date> fechaCol;
    @FXML
    private JFXTextField campoNumero;
    @FXML
    private JFXTextField campoPrecio;
    @FXML
    private JFXTextField campoParticipaciones;
    @FXML
    private JFXButton botonRefresh;
    @FXML
    private JFXSnackbar notificationBar;
    @FXML
    private JFXComboBox<String> empresaComboBox;

    @FXML
    public void initialize() {

        datosTabla = FXCollections.observableArrayList();

        // Recuperamos el usuario
        usr = super.getDAO(UsuarioDAO.class).seleccionar(new SuperUsuario.Builder("eva").build());

        // Setup de las columnas de la tabla
        ofertadasCol.setCellValueFactory(new PropertyValueFactory<>("numParticipaciones"));
        restantesCol.setCellValueFactory(new PropertyValueFactory<>("restantes"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tablaOfertas.setItems(datosTabla);

        tablaOfertas.setSelectionModel(null); // Evitamos que se seleccionen filas (estético)
        tablaOfertas.setPlaceholder(new Label("")); // Eliminamos el texto por defecto (estético)
        empresaCol.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(p.getValue().getEmpresa().getNombre());
            } else {
                return new SimpleStringProperty("<no name>");
            }
        });

        // Formato Integer para campoNumero y Float para campoPrecio
        campoNumero.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter(),
                null,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null));
        campoPrecio.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> Pattern.compile("\\d*|\\d+\\.\\d{0,2}").matcher(change.getControlNewText()).matches() ? change : null));

        // Cargamos saldo y preparamos botones de refresh
        botonRefresh.setGraphic(Iconos.icono(FontAwesomeIcon.REFRESH, "1em"));

        //actualizarVentana();
    }

    // FUNCIONALIDADES //
    /*
    public void actualizarSaldo() {
        usr = getDAO(UsuarioDAO.class).seleccionar(new SuperUsuario.Builder(usr.getIdentificador()).build());
        campoSaldo.setText(String.valueOf(usr.getSaldo()-usr.getSaldoBloqueado()));
    }

    public void actualizarListaEmpresas(){
        // Se carga la nueva lista
        listaEmpresas = getDAO(EmpresaDAO.class).getEmpresas();
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
        String identificador = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getUsuario().getIdentificador();
        datosTabla.setAll(getDAO(OfertaVentaDAO.class).getOfertasVenta(identificador, campoPrecio.getText().isEmpty()? Float.parseFloat(campoPrecio.getText()) : 0f));
    }

    public void actualizarVentana(){
        actualizarSaldo();
        actualizarListaEmpresas();
        actualizarDatosTabla();
        campoNumero.setText("");
        campoPrecio.setText("");
    }

    // BOTONES //

    // Boton de salir
    public void btnSalirEvent(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
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
    public void btnRefreshEvent(ActionEvent event){
        actualizarVentana();
    }*/
}
