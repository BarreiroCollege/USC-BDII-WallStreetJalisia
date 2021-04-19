package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.DoubleValidator;
import com.jfoenix.validation.IntegerValidator;
import gal.sdc.usc.wallstreet.model.Empresa;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.model.OfertaVenta;
import gal.sdc.usc.wallstreet.repository.CompraDAO;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.model.Usuario;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
import gal.sdc.usc.wallstreet.repository.helpers.DatabaseLinker;
import gal.sdc.usc.wallstreet.util.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class VCompraController extends DatabaseLinker {
    @FXML
    private JFXButton btnSalir;
    @FXML
    private JFXTextField campoNumero;
    @FXML
    private JFXComboBox<String> empresaComboBox;
    @FXML
    private JFXTextField campoPrecio;
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

    private List<Empresa> listaEmpresas;
    private ObservableList<OfertaVenta> datosTabla;

    @FXML
    public void initialize() {

        listaEmpresas = new ArrayList<>();
        datosTabla = FXCollections.observableArrayList();

        // Setup de las columnas de la tabla
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precio_venta"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<>("num_participaciones"));
        tablaOfertas.setItems(datosTabla);

        // Llenamos la lista de empresas y el ComboBox
        for (Empresa e : getDAO(EmpresaDAO.class).getEmpresas()) {
            listaEmpresas.add(e);
            empresaComboBox.getItems().add(e.getNombre() + " || " + e.getCif());
        }

        // Formato Integer para campoNumero
        campoNumero.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter(),
                null,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null));

        // Formato Float para campoPrecio
        campoPrecio.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> Pattern.compile("\\d*|\\d+.\\d{0,2}").matcher(change.getControlNewText()).matches() ? change : null));

        // Cuando se cambie el precio se actualizan las ofertas en base al nuevo
        campoPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && empresaComboBox.getSelectionModel().getSelectedIndex() != -1)
                actualizarDatosTabla();
        });
    }

    public void btnComprarEvent(ActionEvent event) {
        // Si alguno de los campos necesarios está vacío, no se hace nada
        if (campoPrecio.getText().isEmpty() || campoNumero.getText().isEmpty() || empresaComboBox.getSelectionModel().getSelectedIndex() == -1)
            return;

        // Variables de estado
        Usuario usr;
        double totalprecio = 0;
        Integer compradas = 0;

        if(super.getTipoUsuario().equals(TipoUsuario.EMPRESA)) super.getEmpresa().getUsuario();
        else super.getEmpresa().getUsuario();

        // Se compran de menor a mayor hasta completar o hasta que se quede sin saldo
        super.iniciarTransaccion();
        actualizarDatosTabla(); //Recojemos los datos actualizados


        for(OfertaVenta oferta : datosTabla){

        }
        while (!datosTabla.isEmpty() && totalprecio >= 0) {
            ofertaMenor = ofertas.get(seleccionar_MenorPrecio(ofertas));
            if (ofertaMenor.getNumParticipaciones() <= Integer.parseInt(this.campoNumero.getText())) {
                getDAO(OfertaVentaDAO.class).cerrarOfertaVenta(ofertaMenor);
            } else {

            }
        }
    }

    // Evento de seleccion de empresa en la comboBox
    public void empresaSelected(ActionEvent event) {
        if (!campoPrecio.getText().isEmpty()) actualizarDatosTabla();
    }

    // Carga las ofertas de venta para esa empresa de igual o menor precio
    public void actualizarDatosTabla() {
        List<OfertaVenta> ofertas;
        Empresa empresa = listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex());
        ofertas = getDAO(CompraDAO.class).getOfertasVenta(empresa.getCif(), Float.parseFloat(campoPrecio.getText()));
        datosTabla.setAll(ofertas);
    }

    // Boton de salida
    public void btnSalirEvent(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
    }


}
