package gal.sdc.usc.wallstreet.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gal.sdc.usc.wallstreet.model.*;
import gal.sdc.usc.wallstreet.repository.SuperUsuarioDAO;
import gal.sdc.usc.wallstreet.repository.UsuarioDAO;
import gal.sdc.usc.wallstreet.repository.VentaDAO;
import gal.sdc.usc.wallstreet.repository.EmpresaDAO;
import gal.sdc.usc.wallstreet.repository.OfertaVentaDAO;
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

public class VCompraController extends DatabaseLinker {
    public static final String VIEW = "vcompra";
    public static final Integer HEIGHT = 425;
    public static final Integer WIDTH = 760;
    public static final String TITULO = "Comprar";

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
    @FXML
    private JFXSnackbar notificationBar;

    private Usuario usr;
    private List<Empresa> listaEmpresas;
    private ObservableList<OfertaVenta> datosTabla;

    @FXML
    public void initialize() {

        listaEmpresas = new ArrayList<>();
        datosTabla = FXCollections.observableArrayList();

        // Recuperamos el usuario
        // TODO Recoger el usuario de la sesion
        usr = super.getDAO(UsuarioDAO.class).seleccionar(new SuperUsuario.Builder("eva").build());

        // Setup de las columnas de la tabla
        //nombreCol.setCellValueFactory(new PropertyValueFactory<>("usuario"));
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

        // Cargamos saldo y preparamos botones de refresh
        botonRefresh.setGraphic(Iconos.icono(FontAwesomeIcon.REFRESH, "1em"));

        actualizarVentana();
    }

    // FUNCIONALIDADES //

    public void actualizarSaldo() {
        usr = super.getDAO(UsuarioDAO.class).seleccionar(new SuperUsuario.Builder("eva").build());
        campoSaldo.setText(String.valueOf(usr.getSaldo()-usr.getSaldoBloqueado()));
    }

    public void actualizarListaEmpresas(){
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
        datosTabla.setAll(getDAO(OfertaVentaDAO.class).getOfertasVenta(identificador, campoPrecio.getText().isEmpty()? 0f : Float.parseFloat(campoPrecio.getText())));
    }

    public void actualizarVentana(){
        actualizarSaldo();
        actualizarListaEmpresas();
        actualizarDatosTabla();
        campoNumero.setText("");
        campoPrecio.setText("");
    }

    public void comprar(){
        // Si alguno de los campos necesarios está vacío o es 0 no se hace nada
        if (campoPrecio.getText().isEmpty() || Float.parseFloat(campoPrecio.getText())==0 || campoNumero.getText().isEmpty() || Integer.parseInt(campoNumero.getText())==0 || empresaComboBox.getSelectionModel().getSelectedIndex() == -1)
            return;

        // Variables de estado
        float saldo, saldoInicial;
        int acomprar,partPosibles,compradas = 0;

        // INICIAMOS TRANSACCION
        super.iniciarTransaccion();

        //Recojemos los datos actualizados
        actualizarDatosTabla();
        actualizarSaldo();
        saldo = Float.parseFloat(campoSaldo.getText());
        saldoInicial = saldo;
        acomprar = Integer.parseInt(campoNumero.getText());

        // Compramos de las ofertas de más baratas a las más caras, ya ordenadas en la tabla
        for (OfertaVenta oferta : datosTabla) {
            // Si se compraron las solicitadas o no hay dinero para más se para el bucle
            if (acomprar == compradas) break;
            if ((partPosibles = (int) Math.floor(saldo / oferta.getPrecioVenta())) == 0) break;

            // Se elige el minimo entre las restantes en la oferta, las que quedan por comprar para cubrir el cupo
            // y las que se pueden comprar con el saldo actual
            partPosibles = Math.min(partPosibles, acomprar - compradas);
            partPosibles = Math.min(partPosibles, oferta.getRestantes());

            compradas += partPosibles;
            saldo -= partPosibles * oferta.getPrecioVenta();

            getDAO(VentaDAO.class).insertar(new Venta.Builder().withCantidad(partPosibles)
                                            .withOfertaVenta(oferta)
                                            .withFecha(new Date(System.currentTimeMillis()))
                                            .withUsuarioCompra(usr.getSuperUsuario())
                                            .build());
        }

        // Actualizamos el saldo en la BD
        usr.setSaldo(saldo+usr.getSaldoBloqueado());
        getDAO(UsuarioDAO.class).actualizar(usr);

        // Tratamos de comprometer la transacción e informamos al usuario
        String mensaje;
        if (super.ejecutarTransaccion()) {
            mensaje = "Éxito. Se compraron "+ compradas + " participaciones a una media de " + (saldoInicial-saldo)/compradas + " €/participacion";
        }else{
            mensaje = "Compra fallida!";
        }

        // TODO deberiamos dejar la empresa seleccionada?
        // Actualizamos los elementos gráficos
        notificationBar.enqueue(new JFXSnackbar.SnackbarEvent(new Label(mensaje), Duration.seconds(3.0), null));
        actualizarVentana();
    }

    // BOTONES //

    // Boton de salir
    public void btnVolverEvent(ActionEvent event) {
        ((Stage) btnVolver.getScene().getWindow()).close();
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
    }


}
